package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.domain.address.BaseAddress;
import uz.uzkassa.cts.dto.factura.FacturaCheckStatusDataDto;
import uz.uzkassa.cts.dto.invoice.mappings.InvoiceToFacturaMapping;
import uz.uzkassa.cts.dto.invoice.request.LedgerInvoiceRequestDto;
import uz.uzkassa.cts.dto.invoice.request.ResendToXfileRequest;
import uz.uzkassa.cts.dto.rentinvoice.response.RentInvoiceResponseDto;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.InvoiceType;
import uz.uzkassa.cts.enums.ServiceType;
import uz.uzkassa.cts.filter.TradeInInvoiceFilter;
import uz.uzkassa.cts.mappings.RentInvoiceMapper;
import uz.uzkassa.cts.repository.RentInvoiceRepository;
import uz.uzkassa.cts.repository.RentPaymentHistoryRepository;
import uz.uzkassa.cts.service.*;
import uz.uzkassa.cts.utils.BigDecimalUtil;
import uz.uzkassa.cts.utils.InvoiceCreateExcelUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RentInvoiceServiceImpl extends BaseServiceImpl<RentInvoice, Long> implements RentInvoiceService {

    private final RentInvoiceRepository repository;
    private final XfileService xfileService;
    private final VatService vatService;
    private final LedgerService ledgerService;
    private final RentPaymentHistoryRepository rentPaymentHistoryRepository;
    private final RentOfferService rentOfferService;
    private final ThymeleafService thymeleafService;
    private final RentInvoiceMapper rentInvoiceMapper;

    public RentInvoiceServiceImpl(RentInvoiceRepository repository,
                                  XfileService xfileService,
                                  VatService vatService,
                                  LedgerService ledgerService,
                                  RentPaymentHistoryRepository rentPaymentHistoryRepository,
                                  RentOfferService rentOfferService,
                                  ThymeleafService thymeleafService,
                                  RentInvoiceMapper rentInvoiceMapper) {
        super(repository);
        this.repository = repository;
        this.xfileService = xfileService;
        this.vatService = vatService;
        this.ledgerService = ledgerService;
        this.rentPaymentHistoryRepository = rentPaymentHistoryRepository;
        this.rentOfferService = rentOfferService;
        this.thymeleafService = thymeleafService;
        this.rentInvoiceMapper = rentInvoiceMapper;
    }


    private RentInvoiceResponseDto create(Long offerId, List<RentPaymentHistory> histories) {
        if (histories.isEmpty())
            throw validationService.requiredExceptionThrow("Data").get();

        BigDecimal vatPercent = vatService.getActiveVat().getPercent();

        RentInvoice invoice = new RentInvoice();
        BigDecimal totalSellPrice = BigDecimal.ZERO;
        for (RentPaymentHistory history : histories) {
            int productCount = 1;

            BigDecimal vatPriceFor1 = BigDecimalUtil.calculateVatPriceFor1(history.getAmount(), vatPercent);
            BigDecimal productPriceFor1 = BigDecimalUtil.calculateProductPriceFor1SubtractVatPrice(history.getAmount(), vatPriceFor1);

            RentInvoiceDetail detail = new RentInvoiceDetail();
            detail.setPriceFor1pc(productPriceFor1);
            detail.setVatFor1pc(vatPriceFor1);
            detail.setTotalSellPrice(history.getAmount());
            detail.setTotalVatPrice(vatPriceFor1.multiply(BigDecimal.valueOf(productCount)));
            detail.setDeviceSerialNumber(history.getRentActAccessory().getDeviceSerialNumber());
            detail.setTotalProductCount(productCount);

            Accessory accessory = history.getRentActAccessory().getAccessory();
            detail.setServiceName(accessory.getName());
            detail.setCode(accessory.getCode());
            detail.setPackageCode(accessory.getPackageCode());
            detail.setPackageName(accessory.getPackageName());
            detail.setCatalogCode(accessory.getCatalogCode());
            detail.setCatalogName(accessory.getCatalogName());
            detail.setOrigin(accessory.getOrigin().getId());
            invoice.addDetail(detail);

            totalSellPrice = totalSellPrice.add(history.getAmount());
        }

        BigDecimal totalVat = BigDecimalUtil.calculateVatPriceFor1(totalSellPrice, vatPercent);
        BigDecimal totalProductPrice = BigDecimalUtil.calculateProductPriceFor1SubtractVatPrice(totalSellPrice, totalVat);

        invoice.setTotalSellPrice(totalSellPrice);
        invoice.setTotalPrice(totalProductPrice);
        invoice.setTotalVatPrice(totalVat);
        invoice.setPriceFor1pc(totalProductPrice.divide(BigDecimal.valueOf(histories.size()), MathContext.DECIMAL64));
        invoice.setStatus(ContractStatus.ACCEPTED);
        RentOffer offer = rentOfferService.findById(offerId);
        invoice.setRentOffer(offer);

        saveAndFlash(invoice);

        changeBalance(invoice);
        return rentInvoiceMapper.toDto(invoice);
    }

    @Override
    @Transactional
    public void createInvoiceCron() {
        List<RentPaymentHistory> histories = rentPaymentHistoryRepository.findByInvoiceIdIsNull();
        Map<Long, List<RentPaymentHistory>> map =
                histories.stream()
                        .collect(Collectors.groupingBy(RentPaymentHistory::getOfferId, TreeMap::new, Collectors.toList()));
        log.info(">>>>>>>>>> Start Cron Create Rent Invoice <<<<<<<<<<<<");
        map.forEach(this::createInvoice);
        log.info(">>>>>>>>>> End Cron Create Rent Invoice <<<<<<<<<<<<");
    }

    @Override
    @Transactional
    public void createInvoice(Long offerId, List<RentPaymentHistory> histories) {
        RentInvoiceResponseDto invoice = create(offerId, histories);
        for (RentPaymentHistory history : histories) {
            history.setInvoiceId(invoice.getId());
        }
        rentPaymentHistoryRepository.saveAll(histories);
    }

    private void changeBalance(RentInvoice invoice) {
        RentOffer offer = invoice.getRentOffer();
        LedgerInvoiceRequestDto requestDto = new LedgerInvoiceRequestDto(
            invoice.getId(), offer.getId().toString(), invoice.getId().toString(),
            offer.getNumber(), ServiceType.SERVICE, invoice.getTotalSellPrice(),
            invoice.getCreatedDate().toLocalDate(),
            "/api/rent-invoice/" + invoice.getId() + "/generateTemplate",
            invoice.createDescriptionForLedger()
        );
        Customer customer = offer.getCustomer();
        requestDto.fillCustomerData(customer);

        Company company = offer.getCompany();
        requestDto.fillCompanyData(company);

        ledgerService.payForOrder(requestDto);
    }


    @Override
    public Page<RentInvoiceResponseDto> list(TradeInInvoiceFilter filter) {
        Specification<RentInvoice> specification = getRentInvoiceSpecification(filter);
        return repository.findAll(specification, filter.getOrderedPageable())
            .map(rentInvoiceMapper::toDto);
    }

    private Specification<RentInvoice> getRentInvoiceSpecification(TradeInInvoiceFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            ArrayList<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Join<RentOffer, RentInvoice> contractJoin = root.join(RentInvoice_.RENT_OFFER);
                Join<Customer, RentOffer> customerJoin = contractJoin.join(RentOffer_.CUSTOMER);
                Path<String> tinPath = customerJoin.get(Customer_.TIN);
                Path<String> customerNamePath = customerJoin.get(Customer_.NAME);
                Predicate customerNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerNamePath), filter.getSearchForQuery());
                Predicate tinPred = criteriaBuilder.like(criteriaBuilder.lower(tinPath), filter.getSearchForQuery());
                list.add(criteriaBuilder.or(customerNamePred, tinPred));
            }
            if (filter.getId() != null) {
                Path<String> id = root.get(RentInvoice_.ID);
                list.add(criteriaBuilder.equal(id, filter.getId()));
            }
            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get(RentInvoice_.INVOICE_DATE);
                list.add(criteriaBuilder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }
            if (filter.getStatus() != null) {
                Path<ContractStatus> status = root.get(RentInvoice_.STATUS);
                list.add(criteriaBuilder.equal(status, ContractStatus.valueOf(filter.getStatus())));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
    }

    @Override
    public RentInvoiceResponseDto getById(Long id) {
        return rentInvoiceMapper.toDto(findById(id));
    }

    @Override
    public String getHtmlTemplate(Long id) {
        return thymeleafService.generateRentInvoice(findById(id));
    }

    @Override
    public Page<RentInvoiceResponseDto> getByOfferId(Long offerId, TradeInInvoiceFilter filter) {
        Specification<RentInvoice> specification = getRentInvoiceSpecification(filter);
        return repository.findAll(Specification.where(getByOfferId(offerId)).and(specification), filter.getOrderedPageable())
            .map(rentInvoiceMapper::toDto);
    }

    public static Specification<RentInvoice> getByOfferId(Long offerId) {
        if (offerId != null) {
            return (root, query, cb) -> {
                Join<RentOffer, RentInvoice> offerJoin = root.join(RentInvoice_.RENT_OFFER);
                Path<Long> offerPath = offerJoin.get(RentOffer_.ID);
                return cb.equal(offerPath, offerId);
            };
        }
        return null;
    }

    @Override
    @Transactional
    public void revertInvoice(Long id) {
        RentInvoice invoice = findById(id);
        if (invoice.getStatus() != ContractStatus.ACCEPTED) {
            throw validationService.statusChangeExceptionThrow(invoice.getStatus().getName()).get();
        }
        ledgerService.revertByInvoiceId(invoice.getId(), InvoiceType.RENT_INVOICE);
        invoice.setStatus(ContractStatus.REJECTED);
        save(invoice);
    }

    @Override
    @Transactional
    public void resendToXfileWithNewDate(ResendToXfileRequest dto) {
        RentInvoice invoice = findById(dto.getId());
        invoice.setInvoiceDate(dto.getDate().atStartOfDay());
        String xfileId = xfileService.sendInvoice(InvoiceToFacturaMapping.mapInvoiceToFactura(invoice));
        invoice.setXfileId(xfileId);
        save(invoice);
    }

    @Override
    public byte[] download(LocalDate from, LocalDate to, ContractStatus status) {
        List<RentInvoice> invoices = repository.findAll(getRentInvoiceSpecification(new TradeInInvoiceFilter(from, to, status)));

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("RentInvoice");
            String[] headerNames = new String[]{"ИНН", "Название компании", "Регион", "Адрес", "Номер телефона",
                "МФО", "Расчётный счёт", "Номер с/ф", "Дата с/ф", "Сумма", "Статус"};
            InvoiceCreateExcelUtil.createHeader(headerNames, sheet);
            createBody(invoices, sheet);
            workbook.write(bos);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return bos.toByteArray();
    }

    public void createBody(List<RentInvoice> invoices, XSSFSheet sheet) {
        int rowCount = 1;
        for (RentInvoice invoice : invoices) {
            RentOffer offer = invoice.getRentOffer();
            Customer customer = offer.getCustomer();
            CustomerBank customerBank = customer.getCustomerBank();
            BaseAddress baseAddress = customer.getAddress();

            Row row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue(customer.getTin());
            row.createCell(1).setCellValue(customer.getCompanyName());
            row.createCell(2).setCellValue(baseAddress.getRegion().getNameUzLatin());
            row.createCell(3).setCellValue(baseAddress.getFullAddressAddress());
            row.createCell(4).setCellValue(customer.getPhone());
            row.createCell(5).setCellValue(customerBank.getBank().getCode());
            row.createCell(6).setCellValue(customerBank.getAccountNumber());
            row.createCell(7).setCellValue(invoice.getNumber());
            row.createCell(8).setCellValue(invoice.getCreatedDate().toString().substring(0, 10));
            row.createCell(9).setCellValue(invoice.getTotalSellPrice().doubleValue());
            row.createCell(10).setCellValue(invoice.getStatus().getName());
        }
    }

    @Override
    public RentInvoiceResponseDto checkInvoiceXfileStatus(Long id) {
        RentInvoice invoice = findById(id);
        if (invoice.getXfileId() == null) {
            throw validationService.entityNotFoundExceptionThrow(INVOICE).get();
        }
        FacturaCheckStatusDataDto dto = xfileService.checkFacturaStatus(invoice.getXfileId());
        if (!dto.getStatus().equals(invoice.getXfileStatus())) {
            invoice.setXfileStatus(dto.getStatus());
            save(invoice);
        }
        return rentInvoiceMapper.toDto(invoice);
    }
}
