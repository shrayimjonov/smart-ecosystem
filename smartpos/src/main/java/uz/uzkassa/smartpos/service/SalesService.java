package uz.uzkassa.smartpos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import uz.uzkassa.smartpos.constants.Constants;
import uz.uzkassa.smartpos.domain.postgres.Reference;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductUnit;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.company.CompanyTuronInfo;
import uz.uzkassa.smartpos.domain.postgres.receipt.Receipt;
import uz.uzkassa.smartpos.domain.postgres.receipt.SalesDetail;
import uz.uzkassa.smartpos.domain.postgres.sales.Sales;
import uz.uzkassa.smartpos.domain.postgres.sales.SalesAdditional;
import uz.uzkassa.smartpos.domain.postgres.sales.Sales_;
import uz.uzkassa.smartpos.domain.postgres.terminal.Device;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.domain.postgres.warehouse.Register;
import uz.uzkassa.smartpos.dto.SalesDynamicsDto;
import uz.uzkassa.smartpos.dto.analytics.ReceiptStatDto;
import uz.uzkassa.smartpos.dto.clickhouse.SalesClickhouseDto;
import uz.uzkassa.smartpos.dto.clickhouse.SalesDetailClickhouseDto;
import uz.uzkassa.smartpos.dto.etcsms.request.EtcRequestDto;
import uz.uzkassa.smartpos.dto.integration.agreement.ServiceType;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDTO;
import uz.uzkassa.smartpos.dto.sales.*;
import uz.uzkassa.smartpos.enums.*;
import uz.uzkassa.smartpos.enums.sales.SalesStatus;
import uz.uzkassa.smartpos.enums.sales.SalesType;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.exceptions.EntityNotFound;
import uz.uzkassa.smartpos.filter.SalesFilter;
import uz.uzkassa.smartpos.integration.enums.LanguageEnum;
import uz.uzkassa.smartpos.mappers.SalesMapper;
import uz.uzkassa.smartpos.rabbitmq.MqConstants;
import uz.uzkassa.smartpos.rabbitmq.producer.RabbitMqProducer;
import uz.uzkassa.smartpos.repository.clickhouse.sales.SalesClickhouseRepository;
import uz.uzkassa.smartpos.repository.clickhouse.sales.SalesDetailClickhouseRepository;
import uz.uzkassa.smartpos.repository.postgres.ReferenceRepository;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductBranchRepository;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductUnitRepository;
import uz.uzkassa.smartpos.repository.postgres.company.CompanyTuronInfoRepository;
import uz.uzkassa.smartpos.repository.postgres.fashion.FashionIncomeOfProductRepository;
import uz.uzkassa.smartpos.repository.postgres.fashion.FashionRegisterRepository;
import uz.uzkassa.smartpos.repository.postgres.receipt.ReceiptRepository;
import uz.uzkassa.smartpos.repository.postgres.sales.SalesRepository;
import uz.uzkassa.smartpos.repository.postgres.terminal.DeviceRepository;
import uz.uzkassa.smartpos.repository.postgres.user.UserRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.IncomeOfProductDetailRepository;
import uz.uzkassa.smartpos.repository.postgres.warehouse.RegisterRepository;
import uz.uzkassa.smartpos.security.SecurityUtils;
import uz.uzkassa.smartpos.utils.EncryptionUtils;
import uz.uzkassa.smartpos.utils.FinanceUtils;
import uz.uzkassa.smartpos.utils.LanguageUtils;
import uz.uzkassa.smartpos.utils.MarkingUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesService extends SmartposService {
    private final SalesRepository salesRepository;
    private final SalesMapper salesMapper;
    private final RabbitMqProducer rabbitMqProducer;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final SalesClickhouseRepository salesClickhouseRepository;
    private final SalesDetailClickhouseRepository salesDetailClickhouseRepository;
    private final IncomeOfProductDetailRepository incomeOfProductDetailRepository;
    private final PushkassaNotificationService pushkassaNotificationService;
    private final UserRepository userRepository;
    private final ReceiptRepository receiptRepository;
    private final FashionIncomeOfProductRepository fashionIncomeOfProductRepository;
    private final FashionRegisterRepository fashionRegisterRepository;
    private final ProductUnitRepository productUnitRepository;
    private final RegisterRepository registerRepository;
    private final ProductBranchRepository productBranchRepository;
    private final CompanyTuronInfoRepository companyTuronInfoRepository;
    private final MarkingService markingService;
    private final ReferenceRepository referenceRepository;
    private final EtcSmsService etcSmsService;
    private final DeviceRepository deviceRepository;
    private final Environment environment;

    @Transactional
    public ReceiptDTO save(SalesCreateDto dto, Source source) {
        Optional<Sales> optionalSales = Optional.ofNullable(dto.getUid()).flatMap(salesRepository::findFirstByUid);
        if (optionalSales.isPresent() && optionalSales.get().getStatus() == SalesStatus.COMPLETED) {
            throw new BadRequestException("Продажа с uid: " + dto.getUid() + " уже завершена");
        }
        validateForCreditAndAdvance(dto);
        if (Objects.nonNull(dto.getPromotionId())) {
            Long promotionId = dto.getPromotionId();
            for (var details : dto.getReceiptDetails()) {
                details.setPromotionId(promotionId);
            }
        }

        final boolean warehouse = dto.sendToWarehouse();
        final boolean clickhouse = dto.sendToClickhouse();
        final boolean forceToPrint = dto.isForceToPrint() &&
                StringUtils.isNotBlank(dto.getTerminalSN()) &&
                StringUtils.isBlank(dto.getFiscalUrl());
        Device terminal = null;
        if (StringUtils.isNotBlank(dto.getTerminalSN())) {
            terminal = deviceRepository.findFirstBySerialNumberAndType(dto.getTerminalSN(), DeviceType.FISCAL).orElseThrow(notFound("terminal"));
        }
        Sales sale;
        if (dto.getCompanyId() == null) {
            dto.setCompanyId(getCurrentCompanyId());
        }
        if (dto.getBranchId() == null) {
            dto.setBranchId(getCurrentBranchId());
        }
        if (dto.getCreatedById() == null) {
            dto.setCreatedById(getCurrentUserId());
        }
        if (optionalSales.isPresent()) {
            sale = optionalSales.get();
            sale.setSource(source);
            salesMapper.partialUpdate(dto, sale);
        } else {
            sale = salesMapper.toEntity(dto);
            sale.setSource(source);
        }
        Company company = companyRepository.findById(sale.getCompanyId()).orElseThrow(notFound(localizationService.getMessage("company")));
        Branch branch = branchRepository.findById(sale.getBranchId()).orElseThrow(notFound(localizationService.getMessage("branch")));
        User user = userRepository.findById(sale.getCreatedById()).orElseThrow(notFound(localizationService.getMessage("user")));
        sale.setCompany(company);
        sale.setBranch(branch);
        sale.setCreatedBy(user);
        salesRepository.save(sale);
        if (forceToPrint && terminal != null) {
            Receipt receipt = salesMapper.toReceiptFromSales(sale);
            receipt.setStatus(ReceiptStatus.DRAFT);
            receipt.setReceiptDateTime(LocalDateTime.now());
            receipt.setTerminalId(terminal.getId());
            receiptRepository.save(receipt);
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                if (warehouse) {
                    rabbitMqProducer.send(sale.getUid(), MqConstants.SALES_WAREHOUSE_QUEUE);
                }
                if (clickhouse) {
                    rabbitMqProducer.send(new SalesRabbitMQPayload(sale.getUid()), MqConstants.SALES_CLICKHOUSE_QUEUE);
                }
                if (forceToPrint) {
                    sendToPushkassa(sale);
                }
            }
        });
        return salesMapper.toDto(sale, true, false);
    }

    @Transactional
    public void createSalesFromReceipt(String uid) {
        Receipt receipt = receiptRepository.findOneByUid(uid).orElseThrow(notFound(localizationService.getMessage("receipt")));
        var salesOptional = salesRepository.findFirstByUid(receipt.getUid());
        if (salesOptional.filter(sales -> sales.getStatus() == SalesStatus.COMPLETED).isPresent()) {
            return;
        }
        final Sales sales;
        if (salesOptional.isPresent()) {
            sales = salesMapper.updateCompletedSaleFromReceipt(salesOptional.get(), receipt);
        } else {
            sales = salesMapper.toSalesFromReceipt(receipt);
        }
        salesRepository.save(sales);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                if (List.of(SalesType.SALE, SalesType.RETURNED).contains(sales.getType())) {
                    rabbitMqProducer.send(sales.getUid(), MqConstants.SALES_WAREHOUSE_QUEUE);
                }
                rabbitMqProducer.send(new SalesRabbitMQPayload(sales.getUid()), MqConstants.SALES_CLICKHOUSE_QUEUE);
            }
        });
    }

    private void validateForCreditAndAdvance(SalesCreateDto dto) {
        if (List.of(SalesType.CREDIT, SalesType.ADVANCE).contains(dto.getType())) {
            if (StringUtils.isBlank(dto.getOriginUid())) {
                throw new BadRequestException(localizationService.getMessage("credit.sales.originUid.required"));
            }
        }
    }

    @Transactional
    public SalesDto updateFiscalData(SalesFiscalDto fiscalDto) {
        Optional<Sales> salesOptional = salesRepository.findFirstByUid(fiscalDto.getUid());
        if (salesOptional.isPresent()) {
            Sales sale = salesOptional.get();
            salesMapper.updateFiscalData(sale, fiscalDto);
            sale.setStatus(SalesStatus.COMPLETED);
            salesRepository.save(sale);
            rabbitMqProducer.sendWithDelayTime(sale.getUid(), MqConstants.SALES_RECEIPT_QUEUE, 2000L);
            return salesMapper.toDto(sale);
        }
        return null;
    }

    public void sendToClickhouse(SalesRabbitMQPayload payload) {
        Sales sale = salesRepository.findFirstByUid(payload.uid()).orElseThrow(() -> new EntityNotFound("Sales not found"));
        List<Pair<Long, Long>> productIdAndBranchIds = sale.getSalesDetails().stream()
                .filter(salesDetail -> !Objects.isNull(salesDetail.getProductId()) && !Objects.isNull(salesDetail.getSales().getBranchId()))
                .map(salesDetail -> Pair.of(salesDetail.getProductId(), salesDetail.getSales().getBranchId()))
                .toList();
        Company company = sale.getCompany();
        Map<Pair<Long, Long>, BigDecimal> costPrices;
        if (company.getCompanyType() == CompanyType.FASHION) {
            costPrices = fashionIncomeOfProductRepository.getAvgCostPrices(productIdAndBranchIds);
        } else {
            costPrices = incomeOfProductDetailRepository.getAvgCostPrices(productIdAndBranchIds);
        }
        AtomicReference<BigDecimal> costPrice = new AtomicReference<>(BigDecimal.ZERO);
        if (CollectionUtils.isNotEmpty(sale.getSalesDetails())) {
            sale.getSalesDetails().forEach(salesDetail -> {
                try {
                    BigDecimal eachCost;
                    if (salesDetail.getProductId() != null) {
                        eachCost = costPrices.get(Pair.of(salesDetail.getProductId(), salesDetail.getSales().getBranchId()));
                    } else {
                        eachCost = salesDetail.getPrice();
                    }
                    SalesDetailClickhouseDto detailClickhouseDto = new SalesDetailClickhouseDto(salesDetail, eachCost, objectMapper);
                    kafkaTemplate.send("sales-detail", objectMapper.writeValueAsString(detailClickhouseDto));
                    costPrice.set(costPrice.get().add(detailClickhouseDto.getCostAmount()));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        SalesClickhouseDto dto = new SalesClickhouseDto(sale, costPrice.get(), objectMapper);
        try {
            kafkaTemplate.send("sales", objectMapper.writeValueAsString(dto));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendToPushkassa(Sales sale) {
        var receiptDTO = salesMapper.toDto(sale, true, false);
        pushkassaNotificationService.sendNotification(receiptDTO, "smartpos", "receipt", receiptDTO.getTerminalSN());
    }

    public Page<SalesCabinetListDto> findAllByFilter(SalesFilter filter, boolean forAdmin) {
        if (!forAdmin) {
            filter.setCompanyId(getCurrentCompanyId());
            if (!isOwner() && !isBranchAdmin()) {
                filter.setBranchIds(Sets.newHashSet(getCurrentBranchId()));
            }
            if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIds())) {
                SecurityUtils.getCurrentBranchGroupId()
                        .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                                () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                        );
            }
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        if (CollectionUtils.isEmpty(filter.getTypes())) {
            filter.setTypes(List.of(SalesType.SALE, SalesType.RETURNED));
        }
        return salesClickhouseRepository.getSalesList(filter).map(SalesCabinetListDto::new);
    }

    public SalesDto findById(Long id) {
        Sales sales = salesRepository.findById(id).orElseThrow(() -> new EntityNotFound("Sales not found"));
        SalesDto dto = salesMapper.toDto(sales);
        if (!sales.isFiscal()) {
            receiptRepository.findOneByUid(sales.getUid()).ifPresent(receipt -> {
                dto.setFiscalSign(receipt.getFiscalSign());
                dto.setFiscalUrl(receipt.getFiscalUrl());
                dto.setAdditional(receipt.getAdditional());
            });
        }
        return dto;
    }

    public SalesDto findByUid(String uid) {
        SalesDto salesDto = salesRepository.findFirstByUid(uid).map(salesMapper::toDto)
                .orElseThrow(() -> new EntityNotFound("Sales not found"));
        receiptRepository.findOneByUid(salesDto.getUid()).ifPresent(receipt -> {
            salesDto.setFiscalSign(receipt.getFiscalSign());
            salesDto.setFiscalUrl(receipt.getFiscalUrl());
            salesDto.setAdditional(receipt.getAdditional());
        });
        return salesDto;
    }

    public List<SalesTopBranchDto> topBranches(SalesFilter filter) {
        filter.setCompanyId(getCurrentCompanyId());
        if (!isOwner() && !isBranchAdmin()) {
            filter.setBranchIds(Sets.newHashSet(getCurrentBranchId()));
        }
        if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIds())) {
            SecurityUtils.getCurrentBranchGroupId()
                    .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                            () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                    );
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        if (CollectionUtils.isEmpty(filter.getTypes())) {
            filter.setTypes(List.of(SalesType.SALE, SalesType.RETURNED));
        }
        return salesClickhouseRepository.topBranches(filter);
    }

    public ByteArrayResource topBranchesExcel(SalesFilter filter) {
        filter.setCompanyId(getCurrentCompanyId());
        if (!isOwner() && !isBranchAdmin()) {
            filter.setBranchIds(Sets.newHashSet(getCurrentBranchId()));
        }
        if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIds())) {
            SecurityUtils.getCurrentBranchGroupId()
                    .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                            () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                    );
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        if (CollectionUtils.isEmpty(filter.getTypes())) {
            filter.setTypes(List.of(SalesType.SALE, SalesType.RETURNED));
        }
        filter.setPage(0);
        filter.setSize(1000);
        List<SalesTopBranchDto> branches = salesClickhouseRepository.topBranches(filter);
        try (var workbook = new XSSFWorkbook(); var outputStream = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("Топ филиалов");
            var headerRow = sheet.createRow(0);
            var headers = new String[]{"Филиал", "Количество чеков", "Количество товаров", "Выручка", "Валовая прибыль", "Рентабельность"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
            for (int i = 0; i < branches.size(); i++) {
                var row = sheet.createRow(i + 1);
                var branch = branches.get(i);
                row.createCell(0).setCellValue(branch.getName());
                row.createCell(1).setCellValue(branch.getCount());
                row.createCell(2).setCellValue(branch.getPositions());
                row.createCell(3).setCellValue(branch.getTotalSum().doubleValue());
                BigDecimal totalProfit = branch.getTotalSum().subtract(branch.getTotalCostAmount());
                row.createCell(4).setCellValue(totalProfit.doubleValue());
                row.createCell(5).setCellValue(BigDecimal.valueOf(100).multiply(totalProfit)
                        .divide(branch.getTotalCostAmount(), 2, RoundingMode.HALF_UP)
                        .doubleValue());
            }
            workbook.write(outputStream);
            return new ByteArrayResource(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<SalesTopProductDto> topProducts(SalesFilter filter) {
        filter.setCompanyId(getCurrentCompanyId());
        if (!isOwner() && !isBranchAdmin()) {
            filter.setBranchIds(Sets.newHashSet(getCurrentBranchId()));
        }
        if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIds())) {
            SecurityUtils.getCurrentBranchGroupId()
                    .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                            () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                    );
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        if (CollectionUtils.isEmpty(filter.getTypes())) {
            filter.setTypes(List.of(SalesType.SALE, SalesType.RETURNED));
        }
        return salesDetailClickhouseRepository.topProducts(filter);
    }

    public ByteArrayResource topProductsExcel(SalesFilter filter) {
        filter.setCompanyId(getCurrentCompanyId());
        if (!isOwner() && !isBranchAdmin()) {
            filter.setBranchIds(Sets.newHashSet(getCurrentBranchId()));
        }
        if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIds())) {
            SecurityUtils.getCurrentBranchGroupId()
                    .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                            () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                    );
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        if (CollectionUtils.isEmpty(filter.getTypes())) {
            filter.setTypes(List.of(SalesType.SALE, SalesType.RETURNED));
        }
        filter.setPage(0);
        filter.setSize(1000);
        List<SalesTopProductDto> products = salesDetailClickhouseRepository.topProducts(filter);
        try (var workbook = new XSSFWorkbook(); var outputStream = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("Топ товаров");
            var headerRow = sheet.createRow(0);
            var headers = new String[]{"Наименование", "Чистая выручка", "Валовая прибыль", "Количество проданных"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
            for (int i = 0; i < products.size(); i++) {
                var row = sheet.createRow(i + 1);
                var product = products.get(i);
                row.createCell(0).setCellValue(product.getName());
                row.createCell(1).setCellValue(product.getTotalAmount().doubleValue());
                row.createCell(2).setCellValue(product.getTotalAmount().subtract(product.getTotalCostAmount()).doubleValue());
                row.createCell(3).setCellValue(product.getTotalQty().doubleValue());
            }
            workbook.write(outputStream);
            return new ByteArrayResource(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<SalesDynamicsDto> salesDynamics(SalesFilter filter) {
        filter.setCompanyId(getCurrentCompanyId());
        if (!isOwner() && !isBranchAdmin()) {
            filter.setBranchIds(Sets.newHashSet(getCurrentBranchId()));
        }
        if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIds())) {
            SecurityUtils.getCurrentBranchGroupId()
                    .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                            () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                    );
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        if (CollectionUtils.isEmpty(filter.getTypes())) {
            filter.setTypes(List.of(SalesType.SALE, SalesType.RETURNED));
        }
        return salesClickhouseRepository.salesDynamics(filter);
    }

    public SalesDetailDynamicsDto salesDetailDynamics(SalesFilter filter) {
        filter.setCompanyId(getCurrentCompanyId());
        if (!isOwner() && !isBranchAdmin()) {
            filter.setBranchIds(Sets.newHashSet(getCurrentBranchId()));
        }
        if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIds())) {
            SecurityUtils.getCurrentBranchGroupId()
                    .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                            () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                    );
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        if (CollectionUtils.isEmpty(filter.getTypes())) {
            filter.setTypes(List.of(SalesType.SALE, SalesType.RETURNED));
        }
        return salesDetailClickhouseRepository.salesDetailDynamics(filter);
    }

    public SalesReportStatsDto getSalesReportStats(SalesFilter filter) {
        filter.setCompanyId(getCurrentCompanyId());
        if (!isOwner() && !isBranchAdmin()) {
            filter.setBranchIds(Sets.newHashSet(getCurrentBranchId()));
        }
        if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIds())) {
            SecurityUtils.getCurrentBranchGroupId()
                    .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                            () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                    );
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        if (CollectionUtils.isEmpty(filter.getTypes())) {
            filter.setTypes(List.of(SalesType.SALE, SalesType.RETURNED));
        }
        return salesClickhouseRepository.getSalesReportStats(filter);
    }

    public ReceiptStatDto statistics(SalesFilter filter, boolean forAdmin) {
        if (!forAdmin) {
            filter.setCompanyId(getCurrentCompanyId());
            if (!isOwner() && !isBranchAdmin()) {
                filter.setBranchIds(Sets.newHashSet(getCurrentBranchId()));
            }
            if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIds())) {
                SecurityUtils.getCurrentBranchGroupId()
                        .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                                () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                        );
            }
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        if (CollectionUtils.isEmpty(filter.getTypes())) {
            filter.setTypes(List.of(SalesType.SALE, SalesType.RETURNED));
        }
        return salesClickhouseRepository.statistics(filter);
    }

    public Page<SalesCabinetListDto> findAllDraftsByFilter(SalesFilter filter, boolean forAdmin) {
        if (!forAdmin) {
            filter.setCompanyId(getCurrentCompanyId());
            if (!isOwner() && !isBranchAdmin()) {
                filter.setBranchIds(Sets.newHashSet(getCurrentBranchId()));
            }
            if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIds())) {
                SecurityUtils.getCurrentBranchGroupId()
                        .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                                () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                        );
            }
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        filter.setStatus(SalesStatus.DRAFT);
        return salesRepository.findAll((Specification<Sales>) (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.isNull(root.get(Sales_.facturaId)),
                    criteriaBuilder.and(
                            criteriaBuilder.isNotNull(root.get(Sales_.facturaId)),
                            criteriaBuilder.isFalse(criteriaBuilder.function("jsonb_exists", Boolean.class, root.get(Sales_.additional), criteriaBuilder.literal("customerPhone"))))
            ));
            if (filter.getCompanyId() != null) {
                predicates.add(criteriaBuilder.equal(root.get(Sales_.companyId), filter.getCompanyId()));
            }
            if (filter.getBranchId() != null) {
                predicates.add(criteriaBuilder.equal(root.get(Sales_.branchId), filter.getBranchId()));
            }
            if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
                predicates.add(root.get(Sales_.branchId).in(filter.getBranchIds()));
            }
            if (filter.getFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(Sales_.createdDateTime), filter.getFrom()));
            }
            if (filter.getTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(Sales_.createdDateTime), filter.getTo()));
            }
            if (filter.getUserId() != null) {
                predicates.add(criteriaBuilder.equal(root.get(Sales_.createdById), filter.getUserId()));
            }
            if (filter.getSource() != null) {
                predicates.add(criteriaBuilder.equal(root.get(Sales_.source), filter.getSource()));
            }
            if (filter.getType() != null) {
                predicates.add(criteriaBuilder.equal(root.get(Sales_.type), filter.getType()));
            }
            if (filter.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get(Sales_.status), filter.getStatus()));
            }
            if (filter.getShiftNumber() != null) {
                predicates.add(criteriaBuilder.equal(root.get(Sales_.shiftNumber), filter.getShiftNumber()));
            }
            if (filter.getPaymentType() != null) {
                predicates.add(criteriaBuilder.isTrue(criteriaBuilder.function("jsonb_exists", Boolean.class, root.get(Sales_.payments), criteriaBuilder.literal(filter.getPaymentType().name()))));
            }
            if (filter.getTotalCostFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(Sales_.totalCost), filter.getTotalCostFrom()));
            }
            if (filter.getTotalCostTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(Sales_.totalCost), filter.getTotalCostTo()));
            }
            query.orderBy(criteriaBuilder.desc(root.get(Sales_.id)));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, filter.getPageable()).map(SalesCabinetListDto::new);
    }

    public SalesDto findByUidForReturn(String uid) {
        Optional<Sales> salesOptional = salesRepository.findFirstByUid(uid);
        if (salesOptional.isPresent()) {
            if (salesOptional.get().getType() != SalesType.SALE) {
                throw new BadRequestException("Для возврата чека необходимо иметь тип продажи");
            }
        }
        Optional<Receipt> receiptOptional = receiptRepository.findOneByUid(uid);
        if (receiptOptional.isPresent()) {
            if (!List.of(ReceiptStatus.DRAFT, ReceiptStatus.PAID).contains(receiptOptional.get().getStatus())) {
                throw new BadRequestException("Для возврата чека необходимо иметь тип продажи");
            }
        }
        if (salesOptional.isEmpty() && receiptOptional.isEmpty()) {
            throw notFound(localizationService.getMessage("sales")).get();
        }
        if (salesOptional.isPresent()) {
            SalesDto dto = salesMapper.toDto(salesOptional.get());
            receiptOptional.filter(receipt -> receipt.getStatus() == ReceiptStatus.PAID)
                    .ifPresent(receipt -> {
                        dto.setFiscalSign(receipt.getFiscalSign());
                        dto.setFiscalUrl(receipt.getFiscalUrl());
                        dto.setAdditional(receipt.getAdditional());
                    });
            return dto;
        }
        return salesMapper.toSalesDtoFromReceipt(receiptOptional.get());
    }

    @Transactional
    public void transit(SalesCreateDto dto) {
        if (dto.getFacturaId() == null) {
            throw new BadRequestException("Фактура не найдена");
        }
        Sales originalSale = salesRepository.findFirstByUid(dto.getUid())
                .orElseThrow(notFound(localizationService.getMessage("sales")));
        final var companyId = Optional.ofNullable(dto.getCompanyId()).orElseGet(this::getCurrentCompanyId);
        final var branchId = Optional.ofNullable(dto.getBranchId()).orElseGet(this::getCurrentBranchId);
        final var userId = Optional.ofNullable(dto.getCreatedById()).orElseGet(this::getCurrentUserId);
        final var groupedReceiptOptional = Optional.ofNullable(dto.getUid())
                .flatMap(receiptRepository::findOneByUid);
        var terminal = Optional.ofNullable(dto.getTerminalSN()).flatMap(serialNumber -> deviceRepository.findFirstBySerialNumberAndType(serialNumber, DeviceType.FISCAL))
                .orElseGet(() -> deviceRepository.findFirstByBranchIdAndType(branchId, DeviceType.FISCAL)
                        .orElseThrow(() -> new BadRequestException("Кассовый аппарат отсутствует в этом филиале")));
        if (groupedReceiptOptional.isPresent()) {
            throw new BadRequestException("Чек уже существует для фискализации");
        }
        if (dto.getStatus() == null) {
            dto.setStatus(SalesStatus.DRAFT);
        }
        final var company = companyRepository.getReferenceById(companyId);
        validateReceipt(dto, branchId, company, true);

        var hasMarks = dto.getReceiptDetails().stream().anyMatch(detail -> CollectionUtils.isNotEmpty(detail.getMarks()));
        final String token = hasMarks ? companyTuronInfoRepository.findById(companyId).map(CompanyTuronInfo::getToken)
                .orElseThrow(() -> new BadRequestException("Токен Турона не найден")) : null;
        var first = dto.getReceiptDetails().getFirst();
        var futures = IntStream.range(1, dto.getReceiptDetails().size()).mapToObj(i -> CompletableFuture.runAsync(() -> {
            SalesDetailCreateDto detail = dto.getReceiptDetails().get(i);
            if (CollectionUtils.isNotEmpty(detail.getMarks())) {
                detail.setMarks(markingService.getAllChild(token, MarkingUtils.getGroupCode(detail.getVatBarcode()), new ArrayList<>(detail.getMarks())));
            }
        })).toList();
        if (CollectionUtils.isNotEmpty(first.getMarks())) {
            first.setMarks(markingService.getAllChild(token, MarkingUtils.getGroupCode(first.getVatBarcode()), new ArrayList<>(first.getMarks())));
        }
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

        final int MAX_POSITION_SIZE = referenceRepository.findByCode(Constants.MAX_DETAIL_SIZE).map(Reference::getValue).map(Integer::parseInt).orElse(15);
        final int MAX_MARKING_SIZE = referenceRepository.findByCode(Constants.MAX_MARKINGS_SIZE).map(Reference::getValue).map(Integer::parseInt).orElse(27);
        var partitionedDetails = partitionReceiptByMarkings(dto, MAX_MARKING_SIZE);
        var sales = mergePartitions(dto, partitionedDetails, MAX_POSITION_SIZE, userId, companyId, branchId, terminal);
        salesRepository.delete(originalSale);
        salesRepository.saveAll(sales);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                AtomicInteger counter = new AtomicInteger(0);
                sales.forEach(receipt -> rabbitMqProducer.sendWithDelayTime(receipt.getId(), MqConstants.RECEIPT_AUTO_PRINT_QUEUE, counter.getAndIncrement() * 1000L));
                sendSms(dto);
            }
        });
    }

    public void sendSms(SalesCreateDto dto) {
        if (EncryptionUtils.checkLogin(dto.getCustomerPhone())) {
            boolean isDev = Arrays.asList(environment.getActiveProfiles()).contains("dev");
            String tail = isDev ? "?dev=1" : "";
            String text = switch (LanguageUtils.getLanguage()) {
                case LanguageEnum.uz ->
                        "Sizning cheklaringiz: https://smartpos.uz/transit/check/" + dto.getFacturaId() + tail;
                case ru, cyrillic -> "Vashi cheki: https://smartpos.uz/transit/check/" + dto.getFacturaId() + tail;
            };
            etcSmsService.sendSms(new EtcRequestDto(dto.getCustomerPhone(), text));
        }
    }

    @NotNull
    private static List<Sales> mergePartitions(SalesCreateDto dto, List<SalesDetail> partitionedDetails, int MAX_POSITION_SIZE, Long userId, Long companyId, Long branchId, Device terminal) {
        return Lists.partition(partitionedDetails, MAX_POSITION_SIZE).stream().map(part -> {
            var entity = new Sales();
            entity.setCreatedDateTime(LocalDateTime.now());
            entity.setReceiptDateTime(LocalDateTime.now());
            entity.setUid(System.currentTimeMillis() + RandomStringUtils.randomNumeric(4) + userId);
            entity.setLat(dto.getLat());
            entity.setLon(dto.getLon());
            entity.setCompanyId(companyId);
            entity.setBranchId(branchId);
            entity.setCreatedById(userId);
            entity.setDiscountPercent(dto.getDiscountPercent());
            entity.setType(SalesType.SALE);
            entity.setStatus(SalesStatus.DRAFT);
            entity.setSource(Source.CASHBOX);
            entity.setAdditional(Optional.ofNullable(dto.getAdditional()).map(SalesAdditional::new).orElseGet(SalesAdditional::new));
            entity.getAdditional().setCustomerName(dto.getCustomerName());
            entity.getAdditional().setCustomerPhone(dto.getCustomerPhone());
            entity.getAdditional().setBillId(dto.getBillId());
            entity.setTerminalSN(terminal.getSerialNumber());
            entity.setFacturaId(dto.getFacturaId());
            BigDecimal partTotalCost = part.stream().map(SalesDetail::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal partTotalDiscount = part.stream().map(SalesDetail::getDiscount).filter(Objects::nonNull).reduce(BigDecimal::add).orElse(null);
            BigDecimal partTotalPaid = partTotalCost.subtract(partTotalDiscount != null ? partTotalDiscount : BigDecimal.ZERO);
            BigDecimal partTotalVat = part.stream().map(SalesDetail::getVat).filter(Objects::nonNull).reduce(BigDecimal::add).orElse(null);
            Map<PaymentType, BigDecimal> payments = new HashMap<>();
            if (partTotalDiscount != null) {
                payments.put(PaymentType.DISCOUNT, partTotalDiscount);
            }
            if (partTotalVat != null) {
                payments.put(PaymentType.NDS, partTotalVat);
            }
            EnumSet.complementOf(EnumSet.of(PaymentType.DISCOUNT, PaymentType.NDS)).forEach(type -> {
                if (dto.getPayments().containsKey(type)) {
                    BigDecimal totalPayment = dto.getPayments().get(type);
                    BigDecimal partPayment = partTotalPaid.multiply(totalPayment).divide(dto.getTotalPaid(), 2, RoundingMode.HALF_UP);
                    payments.put(type, partPayment);
                }
            });
            part.forEach(entity::addChild);
            entity.setTotalPaid(partTotalPaid);
            entity.setTotalCost(partTotalCost);
            entity.setPayments(payments);
            return entity;
        }).toList();
    }

    @NotNull
    private List<SalesDetail> partitionReceiptByMarkings(final SalesCreateDto dto, final int MAX_MARKING_SIZE) {
        return dto.getReceiptDetails().stream().flatMap(receiptDetailDto -> {
            if (CollectionUtils.isNotEmpty(receiptDetailDto.getMarks())) {
                final var parts = new ArrayList<SalesDetail>();
                final var marks = new HashSet<String>(MAX_MARKING_SIZE);
                if (receiptDetailDto.getQty().compareTo(BigDecimal.valueOf(receiptDetailDto.getMarks().size())) != 0) {
                    BigDecimal remaining = receiptDetailDto.getQty().subtract(BigDecimal.valueOf(receiptDetailDto.getMarks().size()));
                    parts.add(cloneReceiptDetailData(receiptDetailDto, remaining, null));

                }
                receiptDetailDto.getMarks().forEach(mark -> {
                    marks.add(mark);
                    if (marks.size() == MAX_MARKING_SIZE) {
                        parts.add(cloneReceiptDetailData(receiptDetailDto, BigDecimal.valueOf(MAX_MARKING_SIZE), new HashSet<>(marks)));
                        marks.clear();
                    }
                });
                if (!marks.isEmpty()) {
                    parts.add(cloneReceiptDetailData(receiptDetailDto, BigDecimal.valueOf(marks.size()), marks));
                }
                return parts.stream();
            } else {
                return Stream.of(cloneReceiptDetailData(receiptDetailDto, receiptDetailDto.getQty(), null));
            }
        }).toList();
    }

    public void validateReceipt(SalesCreateDto dto, Long branchId, Company company, boolean fromInvoice) {
        List<Long> productIds = dto.getReceiptDetails().stream().map(SalesDetailCreateDto::getProductId).filter(Objects::nonNull).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(productIds)) {
            return;
        }
        if (fromInvoice) {
            if (CollectionUtils.isEmpty(productIds) || dto.getReceiptDetails().size() != productIds.size()) {
                List<String> productNames = dto.getReceiptDetails().stream().filter(detail -> detail.getProductId() == null).map(SalesDetailCreateDto::getProductName).toList();
                throw new BadRequestException("Товара нет в каталоге: " + productNames);
            }
            if ((!company.isFreeTrial() && !company.getPermissions().getOrDefault(ServiceType.WAREHOUSE, LocalDateTime.MIN).isAfter(LocalDateTime.now())) && !isAdmin() && !isOperator()) {
                throw new BadRequestException("Пожалуйста, купите и активируйте складскую услугу", 400);
            }
        }
        if ((company.isFreeTrial() || company.getPermissions().getOrDefault(ServiceType.WAREHOUSE, LocalDateTime.MIN)
                .isAfter(LocalDateTime.now())) && SalesStatus.COMPLETED.equals(dto.getStatus()) && company.getPermissions().getOrDefault(ServiceType.WEBKASSA, LocalDateTime.MIN).isBefore(LocalDateTime.now())) {
            if (company.getCompanyType() == CompanyType.FASHION) {
                validateForFashion(dto, branchId);
            } else {
                validateForRegular(dto, branchId, productIds);
            }
        }
    }

    private void validateForRegular(SalesCreateDto dto, Long branchId, List<Long> productIds) {
        Map<Pair<Long, Long>, Double> productUnitMap = productUnitRepository.findAllByBranchIdAndCityIdAndProductIdIn(branchId, getBranchCityId(branchId), productIds).stream().collect(Collectors.toMap(productUnit -> Pair.of(productUnit.getProductId(), productUnit.getUnitId()), ProductUnit::getCoefficient));
        Map<Long, Register> productAmountInWarehouseMap = registerRepository.findAllByBranchIdAndProductIdIn(branchId, productIds).stream().collect(Collectors.toMap(Register::getProductId, Function.identity()));
        Map<Long, ProductBranch> productBranchMap = productBranchRepository.findAllByBranchIdAndCityIdAndDeletedIsFalseAndProductIdIn(branchId, getBranchCityId(branchId), productIds).stream().collect(Collectors.toMap(ProductBranch::getProductId, Function.identity()));
        StringBuilder error = new StringBuilder();
        dto.getReceiptDetails().forEach(receiptDetailDTO -> {
            if (receiptDetailDTO.getProductId() != null) {
                BigDecimal amountInReceipt = receiptDetailDTO.getQty().multiply(BigDecimal.valueOf(productUnitMap.getOrDefault(Pair.of(receiptDetailDTO.getProductId(), receiptDetailDTO.getUnitId()), 1D)));
                Register register = productAmountInWarehouseMap.getOrDefault(receiptDetailDTO.getProductId(), new Register());
                if (amountInReceipt.compareTo(register.getBalance()) > 0) {
                    error.append("Недостаточное кол-во в складе ");
                    if (StringUtils.isNotBlank(register.getProductName())) {
                        error.append(register.getProductName()).append(": ").append(register.getBalance()).append("\n");
                    } else {
                        String productName = productBranchMap.containsKey(receiptDetailDTO.getProductId()) ? productBranchMap.get(receiptDetailDTO.getProductId()).getName() : receiptDetailDTO.getProductName();
                        error.append(productName).append(": ").append(register.getBalance()).append("\n");
                    }
                }
            }
        });
        if (StringUtils.isNotBlank(error)) {
            throw new BadRequestException(error.toString());
        }
    }


    private void validateForFashion(SalesCreateDto dto, Long branchId) {
        List<Long> productIds = new ArrayList<>();
        List<Long> productItemIds = new ArrayList<>();
        dto.getReceiptDetails().forEach(receiptDetailDTO -> {
            if (receiptDetailDTO.getExtraDetail() != null && receiptDetailDTO.getExtraDetail().getProductItemId() != null) {
                productItemIds.add(receiptDetailDTO.getExtraDetail().getProductItemId());
            } else if (receiptDetailDTO.getProductId() != null) {
                productIds.add(receiptDetailDTO.getProductId());
            }
        });
        final Map<Long, BigDecimal> productAmountInWarehouseMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(productItemIds)) {
            fashionRegisterRepository.findAllByBranchIdAndProductItemIdIn(branchId, productItemIds)
                    .forEach(fashionRegister -> productAmountInWarehouseMap.put(fashionRegister.getProductItemId(), fashionRegister.getQty()));
        }
        if (CollectionUtils.isNotEmpty(productIds)) {
            fashionRegisterRepository.findAllByBranchIdAndProductIdInAndProductItemIdIsNull(branchId, productIds)
                    .forEach(fashionRegister -> productAmountInWarehouseMap.put(fashionRegister.getProductId(), fashionRegister.getQty()));
        }
        StringBuilder error = new StringBuilder();
        dto.getReceiptDetails().forEach(receiptDetailDTO -> {
            if (receiptDetailDTO.getExtraDetail() != null && receiptDetailDTO.getExtraDetail().getProductItemId() != null) {
                BigDecimal amountInWarehouse = productAmountInWarehouseMap.getOrDefault(receiptDetailDTO.getExtraDetail().getProductItemId(), BigDecimal.ZERO);
                if (amountInWarehouse.compareTo(receiptDetailDTO.getQty()) < 0) {
                    error.append("Недостаточное кол-во в складе ");
                    if (StringUtils.isNotBlank(receiptDetailDTO.getProductName())) {
                        error.append(receiptDetailDTO.getProductName()).append(": ").append(amountInWarehouse).append("\n");
                    }
                }

            } else if (receiptDetailDTO.getProductId() != null) {
                BigDecimal amountInWarehouse = productAmountInWarehouseMap.getOrDefault(receiptDetailDTO.getProductId(), BigDecimal.ZERO);
                if (amountInWarehouse.compareTo(receiptDetailDTO.getQty()) < 0) {
                    error.append("Недостаточное кол-во в складе ");
                    if (StringUtils.isNotBlank(receiptDetailDTO.getProductName())) {
                        error.append(receiptDetailDTO.getProductName()).append(": ").append(amountInWarehouse).append("\n");
                    }
                }
            }
        });
        if (StringUtils.isNotBlank(error)) {
            throw new BadRequestException(error.toString());
        }
    }

    private SalesDetail cloneReceiptDetailData(SalesDetailCreateDto detail, BigDecimal qty, Set<String> marks) {
        var clone = new SalesDetail();
        clone.setProductId(detail.getProductId());
        clone.setProductName(detail.getProductName());
        clone.setProductBarcode(detail.getProductBarcode());
        clone.setVatBarcode(detail.getVatBarcode());
        clone.setUnitId(detail.getUnitId());
        clone.setUnitName(detail.getUnitName());
        clone.setExcise(detail.getExcise());
        clone.setExciseRate(detail.getExciseRate());
        clone.setPrice(detail.getPrice());
        clone.setQty(qty);
        clone.setAmount(clone.getPrice().multiply(qty));
        clone.setDiscountPercent(detail.getDiscountPercent());
        clone.setDiscount(detail.getDiscount() != null ? detail.getDiscount().multiply(qty).divide(detail.getQty(), 2, RoundingMode.HALF_UP) : null);
        clone.setVatPercent(detail.getVatPercent());

        if (CollectionUtils.isNotEmpty(marks)) {
            BigDecimal eachDiscount = Optional.ofNullable(clone.getDiscount()).orElse(BigDecimal.ZERO).divide(qty, 2, RoundingMode.HALF_UP);
            BigDecimal eachPrice = clone.getPrice().subtract(eachDiscount);
            clone.setVat(FinanceUtils.calculateVat(eachPrice, detail.getVatPercent()).multiply(qty));
        } else {
            clone.setVat(FinanceUtils.calculateVat(clone.getAmount().subtract(Optional.ofNullable(clone.getDiscount()).orElse(BigDecimal.ZERO)), detail.getVatPercent()));
        }

        clone.setMarks(marks);
        clone.setCommittentTin(detail.getCommittentTin());
        clone.setCategoryId(detail.getCategoryId());
        clone.setCategoryName(detail.getCategoryName());
        clone.setExtraDetail(detail.getExtraDetail());
        return clone;
    }

    public void sendAutoPrint(Long id) {
        Optional<Sales> receiptOptional = salesRepository.findById(id);
        if (receiptOptional.isEmpty()) {
            throw new BadRequestException("Grouped receipt part not found");
        }
        Receipt receipt = salesMapper.toReceiptFromSales(receiptOptional.get());
        receipt.setStatus(ReceiptStatus.DRAFT);
        receipt.setReceiptDateTime(LocalDateTime.now());
        receiptRepository.save(receipt);
        ReceiptDTO dto = salesMapper.toDto(receiptOptional.get(), true, false);
        pushkassaNotificationService.sendNotification(dto, "smartpos", "receipt", dto.getTerminalSN());
    }
}
