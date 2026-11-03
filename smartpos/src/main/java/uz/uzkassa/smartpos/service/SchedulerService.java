package uz.uzkassa.smartpos.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uz.uzkassa.smartpos.domain.postgres.catalog.CreditSettings;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.dto.clickhouse.SalesClickhouseInfoDto;
import uz.uzkassa.smartpos.dto.integration.CheckReturnedReceiptDto;
import uz.uzkassa.smartpos.dto.receipt.*;
import uz.uzkassa.smartpos.dto.scheduling.CHCreditDto;
import uz.uzkassa.smartpos.dto.scheduling.SchedulePaymentDto;
import uz.uzkassa.smartpos.dto.scheduling.request.ProductScheduleRequest;
import uz.uzkassa.smartpos.dto.scheduling.request.ScheduleRequest;
import uz.uzkassa.smartpos.dto.scheduling.response.MonthlyScheduleResponse;
import uz.uzkassa.smartpos.dto.scheduling.response.ProductScheduleResponse;
import uz.uzkassa.smartpos.dto.scheduling.response.ScheduleResponse;
import uz.uzkassa.smartpos.enums.PaymentType;
import uz.uzkassa.smartpos.enums.ReceiptStatus;
import uz.uzkassa.smartpos.enums.sales.SalesType;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.exceptions.EntityNotFound;
import uz.uzkassa.smartpos.repository.clickhouse.receipt.ClickhouseReceiptGenericRepository;
import uz.uzkassa.smartpos.repository.clickhouse.receiptdetail.ClickhouseReceiptDetailRepository;
import uz.uzkassa.smartpos.repository.clickhouse.sales.SalesClickhouseRepository;
import uz.uzkassa.smartpos.repository.clickhouse.sales.SalesDetailClickhouseRepository;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductBranchRepository;
import uz.uzkassa.smartpos.repository.postgres.receipt.ReceiptRepository;
import uz.uzkassa.smartpos.repository.postgres.sales.SalesRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService extends SmartposService {
    private final ProductBranchRepository productBranchRepository;
    private final ClickhouseReceiptGenericRepository clickhouseReceiptGenericRepository;
    private final ClickhouseReceiptDetailRepository clickhouseReceiptDetailRepository;
    private final SalesRepository salesRepository;
    private final ReceiptRepository receiptRepository;
    private final SalesClickhouseRepository salesClickhouseRepository;
    private final SalesDetailClickhouseRepository salesDetailClickhouseRepository;

    public ScheduleDTO schedule(ScheduleRequestDTO request, boolean withSales) {
        boolean forCredit = request.isForCredit();
        boolean fromSales = false;
        if (StringUtils.isNotBlank(request.getOriginUid())) {
            Optional<CheckReturnedReceiptDto> receiptDTO = clickhouseReceiptGenericRepository.checkReceiptByOriginUid(request.getOriginUid(), List.of(ReceiptStatus.CREDIT, ReceiptStatus.ADVANCE));
            if (receiptDTO.isPresent()) {
                if (receiptDTO.get().status() == ReceiptStatus.CREDIT) {
                    forCredit = true;
                }
            } else {
                if (withSales) {
                    Optional<SalesClickhouseInfoDto> firstOriginByUid = salesClickhouseRepository.findFirstOriginByUid(request.getOriginUid());
                    if (firstOriginByUid.isPresent()) {
                        fromSales = true;
                        if (firstOriginByUid.get().getType() == SalesType.CREDIT) {
                            forCredit = true;
                        }
                    } else {
                        throw new BadRequestException("Не найден чек с указанным originUid");
                    }
                } else {
                    throw new BadRequestException("Не найден чек с указанным originUid");
                }
            }
        }
        if (forCredit) {
            return scheduleForCredit(request, fromSales);
        }
        return scheduleForAdvance(request, fromSales);
    }

    public ScheduleDTO scheduleForCredit(ScheduleRequestDTO request, boolean withSales) {
        ScheduleDTO result = new ScheduleDTO();
        boolean receiptExist = StringUtils.isNotBlank(request.getOriginUid());
        if (receiptExist) {
            if (withSales) {
                var sales = salesRepository.findFirstByUid(request.getOriginUid()).orElseThrow();
                request.setProducts(sales.getSalesDetails().stream()
                        .map(salesDetail -> new ProductScheduleRequestDTO(salesDetail.getProductId(), salesDetail.getQty()))
                        .collect(Collectors.toList()));
                result.setTotalCost(sales.getTotalCost());
                result.setTotalNds(sales.getPayments().get(PaymentType.NDS));
                result.setTotalDiscount(sales.getPayments().get(PaymentType.DISCOUNT));
                result.setDiscountPercent(sales.getDiscountPercent());
            } else {
                receiptRepository.findOneByUid(request.getOriginUid())
                        .ifPresentOrElse(receipt -> {
                            request.setProducts(receipt.getReceiptDetails().stream()
                                    .map(receiptDetailDTO -> new ProductScheduleRequestDTO(receiptDetailDTO.getProductId(), receiptDetailDTO.getQty()))
                                    .collect(Collectors.toList()));
                            result.setTotalCost(receipt.getTotalCost());
                            result.setTotalNds(receipt.getTotalNds());
                            result.setTotalDiscount(receipt.getTotalDiscount());
                            result.setDiscountPercent(receipt.getDiscountPercent());
                        }, () -> {
                            throw new BadRequestException("Не найден чек с указанным originUid");
                        });
            }
        }
        List<Long> productIds = request.getProducts().stream().map(ProductScheduleRequestDTO::getId).collect(Collectors.toList());
        List<ProductBranch> products = productBranchRepository.findAllByBranchIdAndCityIdAndDeletedIsFalseAndProductIdIn(request.getBranchId(),
                getBranchCityId(request.getBranchId()),
                productIds);
        if (CollectionUtils.isEmpty(products)) {
            throw new BadRequestException("Товары не найдены");
        }
        if (products.stream().anyMatch(productBranch -> !productBranch.isCreditEnabled() || productBranch.getCreditSettings() == null)) {
            throw new BadRequestException("Кредит не доступен для одного или нескольких товаров");
        }

        Integer maxMonth = products.stream().map(productBranch -> productBranch.getCreditSettings().getMonth()).max(Integer::compareTo)
                .orElseThrow(() -> new BadRequestException("Не найдены месяцы для кредита"));

        if (!receiptExist) {
            Map<Long, BigDecimal> qtyMap = request.getProducts().stream()
                    .collect(Collectors.toMap(ProductScheduleRequestDTO::getId, ProductScheduleRequestDTO::getQty));

            BigDecimal totalCost = products.stream().map(productBranch -> {
                BigDecimal qty = qtyMap.get(productBranch.getProductId());
                return Optional.ofNullable(productBranch.getCreditSettings().getPrice()).orElseGet(productBranch::getSalesPrice).multiply(qty);
            }).reduce(BigDecimal.ZERO, BigDecimal::add);
            result.setTotalCost(totalCost);
            result.setTotalDiscount(BigDecimal.ZERO.compareTo(request.getTotalDiscount()) == 0 ?
                    totalCost.multiply(request.getDiscountPercent()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP) :
                    request.getTotalDiscount());

            List<ScheduleSettings> settingsList = products.stream().map(productBranch -> {
                ScheduleSettings settings = new ScheduleSettings();
                BigDecimal qty = qtyMap.get(productBranch.getProductId());
                settings.setPrice(Optional.ofNullable(productBranch.getCreditSettings().getPrice()).orElseGet(productBranch::getSalesPrice).multiply(qty));
                BigDecimal vatRate = Optional.ofNullable(productBranch.getVatRate()).orElse(BigDecimal.ZERO);
                settings.setNds(result.getTotalCost().subtract(result.getTotalDiscount())
                        .multiply(settings.getPrice())
                        .multiply(vatRate)
                        .divide(result.getTotalCost().multiply(BigDecimal.valueOf(100).add(vatRate)), 2, RoundingMode.HALF_UP));
                settings.setMonth(productBranch.getCreditSettings().getMonth());
                settings.setPercent(productBranch.getCreditSettings().getPercent());
                return settings;
            }).toList();
            BigDecimal totalNds = settingsList.stream().map(ScheduleSettings::getNds).reduce(BigDecimal.ZERO, BigDecimal::add);
            result.setTotalCost(totalCost);
            result.setTotalNds(totalNds);
        }

        BigDecimal remainingAmount = result.getTotalCost();
        BigDecimal remainingNdsAmount = result.getTotalNds();
        BigDecimal remainingDiscount = result.getTotalDiscount();

        List<CreditPaymentDTO> payments = new ArrayList<>();
        LocalDateTime startTime = LocalDateTime.now();
        if (receiptExist) {
            if (withSales) {
                payments = salesClickhouseRepository.findAllCreditPayments(request.getOriginUid());
            } else {
                payments = clickhouseReceiptGenericRepository.findAllCreditPayments(request.getOriginUid());
            }
            result.setTotalPaid(payments.stream().map(CreditPaymentDTO::getPaid).reduce(BigDecimal.ZERO, BigDecimal::add));
            result.setTotalPaidNds(payments.stream().map(CreditPaymentDTO::getNds).reduce(BigDecimal.ZERO, BigDecimal::add));
            if (CollectionUtils.isNotEmpty(payments)) {
                startTime = payments.getFirst().getPaidDateTime();
            }
        }

        result.setBills(new ArrayList<>(maxMonth));

        for (int i = 0; i < maxMonth; i++) {
            CreditPaymentDTO paymentDTO = null;
            if (payments.size() > i) {
                paymentDTO = payments.get(i);
            }
            BigDecimal discountAmount = paymentDTO != null ? paymentDTO.getDiscount() : remainingDiscount.divide(BigDecimal.valueOf(maxMonth - i), 2, RoundingMode.HALF_UP);
            remainingDiscount = remainingDiscount.subtract(discountAmount);

            BigDecimal dueAmount = remainingAmount.divide(BigDecimal.valueOf(maxMonth - i), 2, RoundingMode.HALF_UP);
            remainingAmount = remainingAmount.subtract(dueAmount);

            BigDecimal dueNdsAmount = paymentDTO != null ? paymentDTO.getNds() : remainingNdsAmount.divide(BigDecimal.valueOf(maxMonth - i), 2, RoundingMode.HALF_UP);
            remainingNdsAmount = remainingNdsAmount.subtract(dueNdsAmount);

            result.getBills().add(new CreditBillDTO(
                    startTime.plusMonths(i),
                    dueAmount,
                    dueNdsAmount,
                    Optional.ofNullable(paymentDTO).map(dto -> Collections.singletonList(dto.getPaidDateTime())).orElse(null),
                    Optional.ofNullable(paymentDTO).map(CreditPaymentDTO::getPaid).orElse(null),
                    Optional.ofNullable(paymentDTO).map(CreditPaymentDTO::getNds).orElse(null),
                    Optional.ofNullable(paymentDTO).map(CreditPaymentDTO::getDiscount).orElse(discountAmount),
                    paymentDTO != null ? 1 : 0,
                    Optional.ofNullable(paymentDTO).map(CreditPaymentDTO::getPayments).orElse(null)
            ));
        }
        return result;
    }

    public ScheduleDTO scheduleForAdvance(ScheduleRequestDTO dto, boolean withSales) {
        if (dto.getTotalDiscount().compareTo(BigDecimal.ZERO) > 0 || dto.getDiscountPercent().compareTo(BigDecimal.ZERO) > 0) {
            throw new BadRequestException("Скидки не доступны для предоплаты");
        }
        ScheduleDTO result = new ScheduleDTO();
        if (StringUtils.isNotBlank(dto.getOriginUid())) {
            if (withSales) {
                var sales = salesClickhouseRepository.findFirstOriginByUid(dto.getOriginUid()).orElseThrow();
                dto.setBranchId(sales.getBranch().getId());
                dto.setProducts(salesDetailClickhouseRepository.findAllBySalesId(Collections.singleton(sales.getId()))
                        .stream().map(salesDetail -> new ProductScheduleRequestDTO(salesDetail.getProductId(), salesDetail.getQty()))
                        .toList());

            } else {
                clickhouseReceiptGenericRepository.findFirstByOriginUid(dto.getOriginUid()).ifPresentOrElse(receiptDTO -> {
                    dto.setBranchId(receiptDTO.getBranchId());
                    dto.setProducts(new ArrayList<>());
                    receiptDTO.getReceiptDetails().forEach(receiptDetailDTO -> {
                        if (receiptDetailDTO.getProductId() != null) {
                            dto.getProducts().add(new ProductScheduleRequestDTO(receiptDetailDTO.getProductId(), receiptDetailDTO.getQty()));
                        }
                    });
                }, () -> {
                    throw notFound(localizationService.getMessage("receipt")).get();
                });
            }
        }

        List<Long> productIds = dto.getProducts().stream().map(ProductScheduleRequestDTO::getId).collect(Collectors.toList());
        List<ProductBranch> products = productBranchRepository.findAllByBranchIdAndCityIdAndDeletedIsFalseAndProductIdIn(dto.getBranchId(),
                getBranchCityId(dto.getBranchId()),
                productIds);
        if (CollectionUtils.isEmpty(products) || dto.getProducts().

                size() != products.size()) {
            throw new EntityNotFound(localizationService.getMessage("product.not.found"));
        }

        Map<Long, BigDecimal> productQuantityMap = dto.getProducts().stream()
                .collect(Collectors.toMap(ProductScheduleRequestDTO::getId, ProductScheduleRequestDTO::getQty));
        if (products.stream().

                anyMatch(productBranch -> !productBranch.isAdvanceEnabled() || productBranch.getAdvanceSettings() == null)) {
            throw new BadRequestException("Отсутствует информация о предоплате за товар");
        }

        List<ScheduleSettings> settings = new ArrayList<>(products.size());
        products.forEach(productBranch ->

        {
            BigDecimal price = productBranch.getSalesPrice();
            ScheduleSettings scheduleSettings = new ScheduleSettings();
            BigDecimal totalPrice = productQuantityMap.get(productBranch.getProductId()).multiply(price);
            scheduleSettings.setPrice(totalPrice);
            BigDecimal vatRate = productBranch.getVatRate() != null ? productBranch.getVatRate() : BigDecimal.ZERO;
            scheduleSettings.setNds(totalPrice.multiply(vatRate).divide(BigDecimal.valueOf(100).add(vatRate), 2, RoundingMode.HALF_UP));
            scheduleSettings.setPercent(productBranch.getAdvanceSettings().getPercent());
            scheduleSettings.setMonth(productBranch.getAdvanceSettings().getMonth() != null ? productBranch.getAdvanceSettings().getMonth() : 2);
            settings.add(scheduleSettings);
        });

        result.setTotalCost(settings.stream().

                map(CreditSettings::getPrice).

                reduce(BigDecimal.ZERO, BigDecimal::add));
        result.setTotalNds(settings.stream().

                map(ScheduleSettings::getNds).

                reduce(BigDecimal.ZERO, BigDecimal::add));
        result.setBills(new ArrayList<>());
        List<CreditPaymentDTO> payments = null;
        if (StringUtils.isNotBlank(dto.getOriginUid())) {
            if (withSales) {
                payments = salesClickhouseRepository.findAllCreditPayments(dto.getOriginUid());
            } else {
                payments = clickhouseReceiptGenericRepository.findAllCreditPayments(dto.getOriginUid());
            }
            result.setTotalPaid(payments.stream().map(CreditPaymentDTO::getPaid).reduce(BigDecimal.ZERO, BigDecimal::add));
            result.setTotalPaidNds(payments.stream().map(CreditPaymentDTO::getNds).reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        LocalDateTime startTime = LocalDateTime.now();
        if (CollectionUtils.isNotEmpty(payments)) {
            startTime = payments.getFirst().getPaidDateTime();
        }

        Integer maxMonth = settings.stream().map(CreditSettings::getMonth).max(Integer::compareTo).orElseThrow(badRequest("Недостаточно данных для оплаты"));

        BigDecimal remainingAmount = result.getTotalCost();
        BigDecimal remainingNdsAmount = result.getTotalNds();

//            first payment

        BigDecimal dueAmount = settings.stream()
                .map(advanceSettings -> advanceSettings.getPrice().multiply(advanceSettings.getPercent()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        remainingAmount = remainingAmount.subtract(dueAmount);
        BigDecimal dueNdsAmount = settings.stream()
                .map(advanceSettings -> advanceSettings.getNds().multiply(advanceSettings.getPercent()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        remainingNdsAmount = remainingNdsAmount.subtract(dueNdsAmount);

        result.getBills().

                add(new CreditBillDTO(startTime,
                        dueAmount,
                        dueNdsAmount,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
//            next payments
        for (
                int i = 1;
                i < maxMonth; i++) {
            dueAmount = remainingAmount.divide(BigDecimal.valueOf(maxMonth - i), 2, RoundingMode.HALF_UP);
            remainingAmount = remainingAmount.subtract(dueAmount);
            dueNdsAmount = remainingNdsAmount.divide(BigDecimal.valueOf(maxMonth - i), 2, RoundingMode.HALF_UP);
            remainingNdsAmount = remainingNdsAmount.subtract(dueNdsAmount);

            result.getBills().add(new CreditBillDTO(
                    startTime.plusMonths(i),
                    dueAmount,
                    dueNdsAmount,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            ));
        }
        if (CollectionUtils.isNotEmpty(payments)) {
            for (int i = 0; i < result.getBills().size(); i++) {
                CreditBillDTO billDTO = result.getBills().get(i);
                if (payments.size() > i) {
                    CreditPaymentDTO paymentDTO = payments.get(i);
                    billDTO.setPaidCount(1);
                    billDTO.setPaidAmount(paymentDTO.getPaid());
                    billDTO.setPaidNdsAmount(paymentDTO.getNds());
                    billDTO.setDiscount(paymentDTO.getDiscount());
                    billDTO.setPayedDateTimes(Collections.singletonList(paymentDTO.getPaidDateTime()));
                    billDTO.setPayments(paymentDTO.getPayments());
                }
            }
        }
        return result;
    }

    public ScheduleResponse schedule(ScheduleRequest request) {
        if (StringUtils.isNotBlank(request.getOriginUid())) {
            fillFromReceipt(request);
        }
        if (request.isForCredit()) {
            return scheduleCredit(request);
        }
        return scheduleAdvance(request);
    }

    private void fillFromReceipt(ScheduleRequest request) {
        CHCreditDto chCreditDto = clickhouseReceiptGenericRepository.findAdditionalByOriginUid(request.getOriginUid())
                .orElseThrow(notFound(localizationService.getMessage("receipt")));
        if (chCreditDto.getMonth().compareTo(0) < 1) {
            throw new BadRequestException("Чек был отправлен неправильно. Атрибут месяца не найден.");
        }
        request.setId(chCreditDto.getId());
        request.setBranchId(chCreditDto.getBranchId());
        request.setMonth(chCreditDto.getMonth());
        request.setForCredit(chCreditDto.isForCredit());
        List<ProductScheduleRequest> details;
        if (request.isForCredit()) {
            details = clickhouseReceiptDetailRepository
                    .findCreditDetails(chCreditDto.getId());
        } else {
            details = clickhouseReceiptDetailRepository
                    .findAdvanceDetails(chCreditDto.getId());
        }
        request.setProducts(details);
    }

    private ScheduleResponse scheduleCredit(ScheduleRequest request) {
        ScheduleResponse response = new ScheduleResponse();
        response.setTotalCost(request.getProducts().stream().map(product -> product.getPrice().multiply(product.getQty())).reduce(BigDecimal.ZERO, BigDecimal::add));
        response.setTotalVat(request.getProducts().stream()
                .map(product -> Optional.ofNullable(product.getVatPercent())
                        .map(vat -> product.getPrice().multiply(product.getQty()).multiply(vat).divide(BigDecimal.valueOf(100).add(vat), 2, RoundingMode.HALF_UP))
                        .orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        var productList = new ArrayList<ProductScheduleResponse>();
        request.getProducts().forEach(product -> {
            ProductScheduleResponse productScheduleResponse = new ProductScheduleResponse();
            productScheduleResponse.setId(product.getId());
            productScheduleResponse.setPrice(product.getPrice());
            productScheduleResponse.setQty(product.getQty());
            productScheduleResponse.setTotalCost(product.getPrice().multiply(product.getQty()));
            productScheduleResponse.setVatPercent(product.getVatPercent());
            productScheduleResponse.setTotalVat(Optional.ofNullable(product.getVatPercent())
                    .map(vat -> productScheduleResponse.getTotalCost().multiply(vat).divide(BigDecimal.valueOf(100).add(vat), 2, RoundingMode.HALF_UP))
                    .orElse(null));
            response.getProducts().add(productScheduleResponse);
            productList.add(productScheduleResponse.clone());
        });
        List<SchedulePaymentDto> payments = clickhouseReceiptGenericRepository.findAllCreditPayments(request.getId());
        LocalDateTime startTime = !payments.isEmpty() ? payments.getFirst().paidDateTime() : LocalDateTime.now();
        IntStream.range(0, request.getMonth()).forEach(i -> {
            MonthlyScheduleResponse monthlySchedule = new MonthlyScheduleResponse();
            monthlySchedule.setDueDateTime(startTime.plusMonths(i));
            if (CollectionUtils.isNotEmpty(payments) && payments.size() > i) {
                SchedulePaymentDto payment = payments.get(i);
                monthlySchedule.setTotalPaid(payment.paid());
                monthlySchedule.setTotalPaidVat(payment.vat());
                monthlySchedule.setPaidDateTime(payment.paidDateTime());
            }
            productList.forEach(product -> {
                BigDecimal totalCost = product.getTotalCost().divide(BigDecimal.valueOf(request.getMonth() - i), 2, RoundingMode.HALF_UP);
                BigDecimal vat = Optional.ofNullable(product.getTotalVat())
                        .map(v -> v.divide(BigDecimal.valueOf(request.getMonth() - i), 2, RoundingMode.HALF_UP))
                        .orElse(null);
                BigDecimal price = totalCost.divide(product.getQty(), 2, RoundingMode.HALF_UP);
                ProductScheduleResponse productSchedule = new ProductScheduleResponse();
                productSchedule.setId(product.getId());
                productSchedule.setTotalCost(totalCost);
                productSchedule.setTotalVat(vat);
                productSchedule.setTotalPaid(payments.size() > i ? payments.get(i).paid() : null);
                productSchedule.setTotalPaidVat(payments.size() > i ? payments.get(i).vat() : null);
                productSchedule.setVatPercent(product.getVatPercent());
                productSchedule.setPrice(price);
                productSchedule.setQty(product.getQty());
                monthlySchedule.getProducts().add(productSchedule);
                product.setTotalCost(product.getTotalCost().subtract(totalCost));
                product.setTotalVat(Optional.ofNullable(product.getTotalVat()).map(v -> v.subtract(vat)).orElse(null));
            });
            monthlySchedule.setTotalCost(monthlySchedule.getProducts().stream().map(ProductScheduleResponse::getTotalCost).reduce(BigDecimal.ZERO, BigDecimal::add));
            monthlySchedule.setTotalVat(monthlySchedule.getProducts().stream().map(p -> Optional.ofNullable(p.getTotalVat()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO, BigDecimal::add));
            response.getMonthlySchedules().add(monthlySchedule);
        });
        return response;
    }

    private ScheduleResponse scheduleAdvance(ScheduleRequest request) {
        ScheduleResponse response = new ScheduleResponse();
        response.setTotalCost(request.getProducts().stream().map(product -> product.getPrice().multiply(product.getQty())).reduce(BigDecimal.ZERO, BigDecimal::add));
        response.setTotalVat(request.getProducts().stream()
                .map(product -> Optional.ofNullable(product.getVatPercent())
                        .map(vat -> product.getPrice().multiply(product.getQty()).multiply(vat).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP))
                        .orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        var productList = new ArrayList<ProductScheduleResponse>();
        request.getProducts().forEach(product -> {
            ProductScheduleResponse productScheduleResponse = new ProductScheduleResponse();
            productScheduleResponse.setId(product.getId());
            productScheduleResponse.setPrice(product.getPrice());
            productScheduleResponse.setQty(product.getQty());
            productScheduleResponse.setTotalCost(product.getPrice().multiply(product.getQty()));
            productScheduleResponse.setVatPercent(product.getVatPercent());
            productScheduleResponse.setTotalVat(Optional.ofNullable(product.getVatPercent())
                    .map(vat -> productScheduleResponse.getTotalCost().multiply(vat).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP))
                    .orElse(null));
            productScheduleResponse.setAdvancePercent(product.getAdvancePercent());
            response.getProducts().add(productScheduleResponse);
            productList.add(productScheduleResponse.clone());
        });
        List<SchedulePaymentDto> payments = clickhouseReceiptGenericRepository.findAllCreditPayments(request.getId());
        LocalDateTime startTime = !payments.isEmpty() ? payments.getFirst().paidDateTime() : LocalDateTime.now();

//        first payment

        MonthlyScheduleResponse firstMonth = new MonthlyScheduleResponse();
        firstMonth.setDueDateTime(startTime);
        if (CollectionUtils.isNotEmpty(payments)) {
            SchedulePaymentDto payment = payments.getFirst();
            firstMonth.setTotalPaid(payment.paid());
            firstMonth.setTotalPaidVat(payment.vat());
            firstMonth.setPaidDateTime(payment.paidDateTime());
        }
        productList.forEach(product -> {
            boolean hasPaid = !payments.isEmpty();
            BigDecimal totalCost = product.getTotalCost().multiply(product.getAdvancePercent())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal vat = Optional.ofNullable(product.getTotalVat())
                    .map(v -> v.multiply(product.getAdvancePercent())
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP))
                    .orElse(null);
            BigDecimal price = product.getPrice().multiply(product.getAdvancePercent())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            ProductScheduleResponse productSchedule = new ProductScheduleResponse();
            productSchedule.setId(product.getId());
            productSchedule.setTotalCost(totalCost);
            productSchedule.setTotalVat(vat);
            productSchedule.setTotalPaid(hasPaid ? payments.getFirst().paid() : null);
            productSchedule.setTotalPaidVat(hasPaid ? payments.getFirst().vat() : null);
            productSchedule.setVatPercent(product.getVatPercent());
            productSchedule.setPrice(price);
            productSchedule.setQty(product.getQty());
            firstMonth.getProducts().add(productSchedule);
            product.setTotalCost(product.getTotalCost().subtract(totalCost));
            product.setTotalVat(Optional.ofNullable(product.getTotalVat()).map(v -> v.subtract(vat)).orElse(null));
        });
        firstMonth.setTotalCost(firstMonth.getProducts().stream().map(ProductScheduleResponse::getTotalCost).reduce(BigDecimal.ZERO, BigDecimal::add));
        firstMonth.setTotalVat(firstMonth.getProducts().stream().map(p -> Optional.ofNullable(p.getTotalVat()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO, BigDecimal::add));
        response.getMonthlySchedules().add(firstMonth);

//        second payment
        MonthlyScheduleResponse secondMonth = new MonthlyScheduleResponse();
        secondMonth.setDueDateTime(startTime.plusMonths(1));
        if (CollectionUtils.isNotEmpty(payments) && payments.size() > 1) {
            SchedulePaymentDto payment = payments.get(1);
            secondMonth.setTotalPaid(payment.paid());
            secondMonth.setTotalPaidVat(payment.vat());
            secondMonth.setPaidDateTime(payment.paidDateTime());
        }
        productList.forEach(product -> {
            BigDecimal totalCost = product.getTotalCost();
            BigDecimal vat = product.getTotalVat();
            BigDecimal price = totalCost.divide(product.getQty(), 2, RoundingMode.HALF_UP);
            secondMonth.getProducts().add(new ProductScheduleResponse() {{
                setId(product.getId());
                setTotalCost(totalCost);
                setTotalVat(vat);
                setTotalPaid(payments.size() > 1 ? payments.get(1).paid() : null);
                setTotalPaidVat(payments.size() > 1 ? payments.get(1).vat() : null);
                setVatPercent(product.getVatPercent());
                setPrice(price);
                setQty(product.getQty());
            }});
        });
        response.getMonthlySchedules().add(secondMonth);
        return response;
    }
}

