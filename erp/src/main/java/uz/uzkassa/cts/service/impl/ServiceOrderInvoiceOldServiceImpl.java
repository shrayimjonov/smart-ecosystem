package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.factura.FacturaCheckStatusDataDto;
import uz.uzkassa.cts.dto.invoice.mappings.InvoiceToFacturaMapping;
import uz.uzkassa.cts.dto.invoice.request.ChangeInvoiceOwnerRequestDto;
import uz.uzkassa.cts.dto.invoice.request.InvoiceCalculateDto;
import uz.uzkassa.cts.dto.invoice.request.LedgerInvoiceRequestDto;
import uz.uzkassa.cts.dto.invoice.request.ResendToXfileRequest;
import uz.uzkassa.cts.dto.ledger.response.LedgerResponseDto;
import uz.uzkassa.cts.dto.serviceorderinvoice.request.RevertInvoiceRequestDto;
import uz.uzkassa.cts.dto.serviceorderinvoice.request.ServiceInvoiceRequestDto;
import uz.uzkassa.cts.dto.serviceorderinvoice.response.ServiceOrderInvoiceListDto;
import uz.uzkassa.cts.dto.serviceorderinvoice.response.ServiceOrderInvoiceResponseDto;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.dto.vat.VatInvoiceCalculationDto;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.ServiceOrderInvoiceFilter;
import uz.uzkassa.cts.mappings.ServiceOrderInvoiceMapper;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqDeviceSwitcherProducer;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqXfileProducer;
import uz.uzkassa.cts.repository.CtsServiceOrderSpecificationRepository;
import uz.uzkassa.cts.repository.ServiceOrderInvoiceDetailRepository;
import uz.uzkassa.cts.repository.ServiceOrderInvoiceRepository;
import uz.uzkassa.cts.repository.ServicePaymentHistoryRepository;
import uz.uzkassa.cts.service.*;
import uz.uzkassa.cts.utils.BigDecimalUtil;
import uz.uzkassa.cts.utils.CalcUtils;
import uz.uzkassa.cts.utils.InvoiceCreateExcelUtil;
import uz.uzkassa.cts.utils.NumberToWordUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
@Slf4j
public class ServiceOrderInvoiceOldServiceImpl extends BaseServiceImpl<ServiceOrderInvoice, Long> implements ServiceOrderInvoiceOldService {

    private final CtsServiceOrderSpecificationRepository serviceOrderSpecificationRepository;
    private final LedgerService ledgerService;
    private final ThymeleafService thymeleafService;
    private final FileUploadService fileUploadService;
    private final ServiceOrderInvoiceRepository repository;
    private final ServicePaymentHistoryRepository paymentHistoryRepository;
    private final RabbitMqDeviceSwitcherProducer rabbitMqProducer;
    private final RabbitMqXfileProducer xfileProducer;
    private final ContractService contractService;
    private final XfileService xfileService;
    private final ServiceOrderInvoiceDetailRepository serviceOrderInvoiceDetailRepository;
    private final VatService vatService;
    private final ServiceOrderInvoiceMapper serviceOrderInvoiceMapper;

    public ServiceOrderInvoiceOldServiceImpl(ServiceOrderInvoiceRepository repository,
                                             CtsServiceOrderSpecificationRepository serviceOrderSpecificationRepository,
                                             LedgerService ledgerService,
                                             ThymeleafService thymeleafService,
                                             FileUploadService fileUploadService,
                                             ServicePaymentHistoryRepository paymentHistoryRepository,
                                             RabbitMqDeviceSwitcherProducer rabbitMqProducer,
                                             RabbitMqXfileProducer xfileProducer,
                                             ContractService contractService,
                                             XfileService xfileService,
                                             ServiceOrderInvoiceDetailRepository serviceOrderInvoiceDetailRepository,
                                             VatService vatService,
                                             ServiceOrderInvoiceMapper serviceOrderInvoiceMapper) {
        super(repository);
        this.repository = repository;
        this.serviceOrderSpecificationRepository = serviceOrderSpecificationRepository;
        this.ledgerService = ledgerService;
        this.thymeleafService = thymeleafService;
        this.fileUploadService = fileUploadService;
        this.paymentHistoryRepository = paymentHistoryRepository;
        this.rabbitMqProducer = rabbitMqProducer;
        this.xfileProducer = xfileProducer;
        this.contractService = contractService;
        this.xfileService = xfileService;
        this.serviceOrderInvoiceDetailRepository = serviceOrderInvoiceDetailRepository;
        this.vatService = vatService;
        this.serviceOrderInvoiceMapper = serviceOrderInvoiceMapper;
    }

    @Override
    public InvoiceCalculateDto calculate(Long specificationId) {
        CtsServiceOrderSpecification specification = serviceOrderSpecificationRepository.findById(specificationId)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(SPECIFICATION));

        LedgerResponseDto ledgerResponseDto = ledgerService.getClientBalance(specification.getContract().getCustomerTin(), ServiceType.SERVICE);

        InvoiceCalculateDto dto = new InvoiceCalculateDto();
        dto.setTotalCanBuyBySpecification(specification.getTotalProductCount());
        dto.setCustomerStatement(ledgerResponseDto.getBalance());
        dto.setCanBuyPc(ledgerResponseDto.getBalance().divide(specification.getTotalPrice(), MathContext.DECIMAL128).intValue());
        dto.setPrice1pc(BigDecimalUtil.setScaleByTwo(specification.getTotalPrice()));

        return dto;
    }

    @Override
    public ServiceOrderInvoiceResponseDto create(ServiceInvoiceRequestDto dto) {
        CtsServiceOrderSpecification specification = serviceOrderSpecificationRepository.findById(dto.getSpecificationId())
                .orElseThrow(validationService.entityNotFoundExceptionThrow(SPECIFICATION));

        ServiceOrderInvoice invoice = new ServiceOrderInvoice();
        invoice.setOrderSpecificationId(specification.getId());
        invoice.setContractId(specification.getContractId());
        invoice.setOrderId(specification.getOrderId());

        List<CtsServiceOrderSpecificationDetail> details = specification.getServiceOrderSpecificationDetails();
        for (CtsServiceOrderSpecificationDetail detail : details) {
            if (PeriodType.MONTHLY.equals(detail.getTariff().getPeriodType()))
                throw validationService.badRequestExceptionThrow(INVOICE_PERIOD).get();

            ServiceOrderInvoiceDetail invoiceDetail = new ServiceOrderInvoiceDetail();
            invoiceDetail.setTariffId(detail.getTariffId());
            invoiceDetail.setTariffName(detail.getTariffName());
            invoiceDetail.setProductCount(detail.getProductCount());
            invoiceDetail.setPrice1pc(detail.getPrice1pc());
            invoiceDetail.setSellPrice1pc(detail.getSellPrice1pc());
            invoiceDetail.setVatPrice1pc(detail.getVatPrice1pc());
            invoiceDetail.setVatRate(detail.getVatRate());
            invoiceDetail.setOverallPrice(detail.getOverallPrice());
            invoiceDetail.setOverallSellPrice(detail.getOverallSellPrice());
            invoiceDetail.setOverallVatPrice(detail.getOverallVatPrice());

            invoice.addChild(invoiceDetail);
        }

        invoice.setTotalPrice(specification.getTotalPrice());
        invoice.setTotalPriceInWord(specification.getTotalPriceInWord());
        invoice.setTotalSellPrice(specification.getTotalSellPrice());
        invoice.setTotalSellPriceInWord(specification.getTotalSellPriceInWord());
        invoice.setTotalVatPrice(specification.getTotalVatPrice());
        invoice.setTotalVatPriceInWord(specification.getTotalVatPriceInWord());
        invoice.setTotalProductCount(specification.getTotalProductCount());
        invoice.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);

        invoice = save(invoice);

        return serviceOrderInvoiceMapper.toDto(invoice);

    }

    @Override
    public void recalculate(Long invoiceId) {
        ServiceOrderInvoice invoice = findById(invoiceId);

        for (ServiceOrderInvoiceDetail invoiceDetail : invoice.getServiceOrderInvoiceDetails()) {
            VatInvoiceCalculationDto calc = CalcUtils.calculateInvoice(invoiceDetail.getOverallSellPrice(), invoiceDetail.getProductCount(), vatService.getActiveVat().getPercent());
            //invoiceDetail.setOverallSellPrice(calc.getTotalPriceWithVat());
            invoiceDetail.setOverallPrice(calc.getTotalPrice());
            invoiceDetail.setOverallVatPrice(calc.getTotalVat());
            //invoiceDetail.setPrice1pc(invoiceDetail.getTariff().getPrice());
            invoiceDetail.setSellPrice1pc(CalcUtils.calculatePriceByHistory(invoice.getCreatedDate(), invoiceDetail.getTariff().getPeriodType(), vatService.getActiveVat().getPercent()));
            invoiceDetail.setVatRate(calc.getVatRate());
        }

        VatInvoiceCalculationDto calc = CalcUtils.calculateInvoice(invoice.getTotalSellPrice(), 1, vatService.getActiveVat().getPercent());
        //invoice.setTotalSellPrice(calc.getTotalPriceWithVat());
        invoice.setTotalPrice(calc.getTotalPrice());
        invoice.setTotalPriceInWord(NumberToWordUtil.digits2Text(BigDecimalUtil.setScaleByTwo(calc.getTotalPrice())));
        invoice.setTotalVatPrice(calc.getTotalVat());
        invoice.setTotalVatPriceInWord(NumberToWordUtil.digits2Text(BigDecimalUtil.setScaleByTwo(calc.getTotalPrice())));
        //invoice.setPrice1pc(invoiceDetail.getTariff().getPrice());

        save(invoice);
    }

    @Override
    public ServiceOrderInvoiceResponseDto getById(Long id) {
        ServiceOrderInvoice invoice = findById(id);
        return serviceOrderInvoiceMapper.toDto(invoice);
    }

    @Override
    public ServiceOrderInvoiceResponseDto uploadFile(Long id, MultipartFile file) {
        ServiceOrderInvoice invoice = findById(id);
        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.SERVICE_INVOICE.name(), DocumentType.SERVICE_INVOICE.name());

        invoice.setFileName(response.getFileName());
        invoice.setContentType(file.getContentType());
        invoice.setFileUri(response.getFileDownloadUri());
        invoice.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        // update invoice with file
        invoice = save(invoice);

        return serviceOrderInvoiceMapper.toDto(invoice);
    }

    @Override
    public String getInvoiceTemplate(Long id) {
        ServiceOrderInvoice invoice = findById(id);
        return thymeleafService.generateServiceInvoiceHTML(invoice);
    }


    @Override
    public void deleteByIdIfNotAccepted(Long id) {
        ServiceOrderInvoice invoice = findById(id);

        delete(invoice);
    }

    @Override
    public Page<ServiceOrderInvoiceResponseDto> getByContractId(Long contractId, BaseFilter filter) {
        return repository.findByContractIdOrderByIdDesc(contractId, filter.getOrderedPageable()).map(serviceOrderInvoiceMapper::toDto);
    }

    @Override
    public List<ServiceOrderInvoiceListDto> getInvoices(LocalDate from, LocalDate to, ContractStatus status) {
        return repository.getInvoiceList(from, to, status);
    }

    @Override
    public List<ServiceOrderInvoice> getInvoicesForXfile(LocalDate from, LocalDate to, ContractStatus status) {
        return repository.getInvoicesForXfile(from, to, status);
    }


    @Override
    public List<ServiceOrderInvoice> resendInvoicesToXfile(LocalDate from, LocalDate to, ContractStatus status) {
        return repository.resendInvoicesToXfile(from, to, status);
    }

    @Override
    public List<ServiceOrderInvoice> getInvoicesForXfileByTin(String tin, LocalDate from, LocalDate to, ContractStatus status) {
        return repository.getInvoicesForXfileByTin(tin, from, to, status);
    }

    @Override
    public List<ServiceOrderInvoice> resendInvoicesForXfileByTin(String tin, LocalDate from, LocalDate to, ContractStatus status) {
        return repository.resendInvoicesForXfileByTin(tin, from, to, status);
    }

    @Override
    @Transactional
    public void revertInvoice(Long id) {
        ServiceOrderInvoice invoice = findById(id);
        if (invoice.getStatus() != ContractStatus.ACCEPTED) {
            throw validationService.statusChangeExceptionThrow(invoice.getStatus().getName()).get();
        }

        //Update all history
        List<ServicePaymentHistory> histories = paymentHistoryRepository.findByServiceOrderInvoiceId(invoice.getId());
        for (ServicePaymentHistory history : histories) {
            history.setServiceOrderInvoiceId(null);
            history.setPaid(false);
        }
        paymentHistoryRepository.saveAll(histories);

        //revert balance
        ledgerService.revertByInvoiceId(invoice.getId(), InvoiceType.SERVICE_ORDER_INVOICE);
        //invoice.setDeleted(true);
        invoice.setStatus(ContractStatus.REJECTED);
        save(invoice);

        //SWITCH DEVICE
        rabbitMqProducer.deviceStatusSwitch(invoice.getContract().getCustomer().getTin());
    }

    @Override
    public void revertInvoiceList(RevertInvoiceRequestDto revertInvoiceRequestDto) {
        for (Long id : revertInvoiceRequestDto.getInvoiceId()) {
            revertInvoice(id);
        }
    }

    @Override
    public String getInvoicesDownloadUrl(LocalDate from, LocalDate to, ContractStatus status) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Invoices");
            String[] headerNames = new String[]{"Номер", "ИНН", "Название компании",
                    "Договор", "Дата дог.", "Счет фак.", "Дата сч/ф.",
                    "Стоимость поставки", "НДС Сумм", "Стоим. пост. c НДС",
                    "Статус", "мфо", "расчетный счет", "Банк"};
            InvoiceCreateExcelUtil.createHeader(headerNames, sheet);
            List<ServiceOrderInvoiceListDto> invoiceListDtos = getInvoices(from, to, status);
            InvoiceCreateExcelUtil.createBody(invoiceListDtos, sheet, InvoiceType.SERVICE_ORDER_INVOICE);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            byte[] bytes = bos.toByteArray();
            return fileUploadService.upload(bytes,
                            "invoices.xls",
                            "application/vnd.ms-excel",
                            (long) bos.size(), 0L,
                            EntityTypeEnum.SERVICE_INVOICE.name(),
                            DocumentType.SERVICE_INVOICE.name())
                    .getFileDownloadUri();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public Page<ServiceOrderInvoiceListDto> getInvoicesList(ServiceOrderInvoiceFilter filter) {
        ResultList<ServiceOrderInvoiceListDto> resultList = repository.getInvoiceList_Spec(filter);
        return new PageImpl<>(resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public void fixInvoices() {
        List<ServiceOrderInvoice> resultList = repository.fixInvoices();
        for (ServiceOrderInvoice invoice : resultList) {
            BigDecimal totalPrice = BigDecimal.ZERO;
            BigDecimal totalSellPrice = BigDecimal.ZERO;
            BigDecimal totalVatPrice = BigDecimal.ZERO;
            int totalProductCount = 0;

            List<ServiceOrderInvoiceDetail> detailLis = invoice.getServiceOrderInvoiceDetails();
            for (ServiceOrderInvoiceDetail detail : detailLis) {

                int deviceCount = 1;

                BigDecimal overallVatPrice = BigDecimalUtil.setScaleByTwo(detail.getOverallSellPrice().multiply(BigDecimal.valueOf(15)).divide(BigDecimal.valueOf(115), MathContext.DECIMAL64));
                BigDecimal overallPrice = BigDecimalUtil.setScaleByTwo(detail.getOverallSellPrice().subtract(overallVatPrice));

                detail.setOverallPrice(overallPrice);
                detail.setOverallVatPrice(overallVatPrice);
                detail.setVatPrice1pc(overallVatPrice);

                totalPrice = totalPrice.add(overallPrice);
                totalSellPrice = totalSellPrice.add(detail.getOverallSellPrice());
                totalVatPrice = totalVatPrice.add(overallVatPrice);
                totalProductCount += deviceCount;

            }

            invoice.setTotalPrice(BigDecimalUtil.setScaleByTwo(totalPrice));
            invoice.setTotalPriceInWord(NumberToWordUtil.digits2Text(BigDecimalUtil.setScaleByTwo(totalPrice)));
            invoice.setTotalSellPrice(BigDecimalUtil.setScaleByTwo(totalSellPrice));
            invoice.setTotalSellPriceInWord(NumberToWordUtil.digits2Text(BigDecimalUtil.setScaleByTwo(totalSellPrice)));
            invoice.setTotalVatPrice(BigDecimalUtil.setScaleByTwo(totalVatPrice));
            invoice.setTotalVatPriceInWord(NumberToWordUtil.digits2Text(BigDecimalUtil.setScaleByTwo(totalVatPrice)));
            invoice.setTotalProductCount(totalProductCount);

        }
        repository.saveAll(resultList);
    }

    @Override
    public void resendInvoiceToXfileWithNewDate(ResendToXfileRequest dto) {
        ServiceOrderInvoice invoice = findById(dto.getId());
        invoice.setInvoiceDate(dto.getDate().atStartOfDay());
        save(invoice);
        xfileProducer.sendServiceOrderInvoice(InvoiceToFacturaMapping.mapInvoiceToFactura(invoice));
    }

    @Override
    @Transactional
    public ServiceOrderInvoiceResponseDto changeInvoiceOwnerAndChangeBalance(ChangeInvoiceOwnerRequestDto dto) {
        ServiceOrderInvoice invoice = findById(dto.getInvoiceId());
        Contract contract = contractService.findByCustomerId(dto.getCustomerId());
        if (contract == null) {
            throw validationService.entityNotFoundExceptionThrow(CUSTOMER, ID, dto.getCustomerId().toString()).get();
        }
        List<ServicePaymentHistory> histories = paymentHistoryRepository.findByServiceOrderInvoiceId(invoice.getId());
        revertInvoice(invoice.getId());
        ServiceOrderInvoice newInvoice = createNewInvoice(invoice, contract);
        for (ServicePaymentHistory history : histories) {
            history.setServiceOrderInvoiceId(newInvoice.getId());
        }
        paymentHistoryRepository.saveAll(histories);
        changeBalance(newInvoice, invoice.getContract().getCustomer().getTin(), invoice.getNumber());
        save(invoice);
        return serviceOrderInvoiceMapper.toDto(newInvoice);
    }

    @Override
    public ServiceOrderInvoiceResponseDto checkInvoiceXfileStatus(Long id) {
        ServiceOrderInvoice invoice = findById(id);
        if (StringUtils.isNotEmpty(invoice.getXFileId())) {
            FacturaCheckStatusDataDto dto = xfileService.checkFacturaStatus(invoice.getXFileId());
            if (!dto.getStatus().equals(invoice.getXfileStatus())) {
                invoice.setXfileStatus(dto.getStatus());
                save(invoice);
            }
        }
        return serviceOrderInvoiceMapper.toDto(invoice);
    }

    @Override
    public Long getDetailsCount(ServiceOrderInvoiceFilter filter) {
        Specification<ServiceOrderInvoiceDetail> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            Path<ServiceOrderInvoice> invoicePath = root.join(ServiceOrderInvoiceDetail_.SERVICE_ORDER_INVOICE, JoinType.INNER);

            ArrayList<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Path<Contract> contract = invoicePath.get(ServiceOrderInvoice_.CONTRACT);
                Path<Customer> customer = contract.get(Contract_.CUSTOMER);

                Path<String> tin = customer.get(Customer_.TIN);
                Path<String> customerName = customer.get(Customer_.name);
                Path<String> customerCompanyName = customer.get(Customer_.COMPANY_NAME);

                Predicate customerNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerName), filter.getSearch());
                Predicate customerCompanyNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerCompanyName), filter.getSearch());
                Predicate tinPred = criteriaBuilder.like(criteriaBuilder.lower(tin), filter.getSearch());
                list.add(criteriaBuilder.or(customerNamePred, tinPred, customerCompanyNamePred));
            }
            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = invoicePath.get(ServiceOrderInvoice_.CREATED_DATE);
                list.add(criteriaBuilder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }
            if (filter.getStatus() != null) {
                Path<ContractStatus> status = invoicePath.get(ServiceOrderInvoice_.STATUS);
                list.add(criteriaBuilder.equal(status, ContractStatus.valueOf(filter.getStatus())));
            }
            if (filter.getTariffId() != null) {
                Path<String> tariffIdPath = root.get(ServiceOrderInvoiceDetail_.TARIFF_ID);
                list.add(criteriaBuilder.equal(tariffIdPath, filter.getTariffId()));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        return serviceOrderInvoiceDetailRepository.count(specification);
    }

    @Override
    public void updateInvoiceXfileStatuses(LocalDate from, LocalDate to) {
        for (ServiceOrderInvoice invoice : repository.updateInvoiceXfileStatuses(from, to)) {
            xfileProducer.updateServiceOrderInvoice(invoice.getId());
        }
    }

    private ServiceOrderInvoice createNewInvoice(ServiceOrderInvoice inv, Contract contract) {
        ServiceOrderInvoice invoice = new ServiceOrderInvoice();
        invoice.setContractId(contract.getId());
        List<ServiceOrderInvoiceDetail> details = inv.getServiceOrderInvoiceDetails();
        for (ServiceOrderInvoiceDetail detail : details) {
            ServiceOrderInvoiceDetail invoiceDetail = new ServiceOrderInvoiceDetail();
            invoiceDetail.setTariffId(detail.getTariffId());
            invoiceDetail.setTariffName(detail.getTariffName());
            invoiceDetail.setProductCount(detail.getProductCount());
            invoiceDetail.setPrice1pc(detail.getPrice1pc());
            invoiceDetail.setSellPrice1pc(detail.getSellPrice1pc());
            invoiceDetail.setVatPrice1pc(detail.getVatPrice1pc());
            invoiceDetail.setVatRate(detail.getVatRate());
            invoiceDetail.setOverallPrice(detail.getOverallPrice());
            invoiceDetail.setOverallSellPrice(detail.getOverallSellPrice());
            invoiceDetail.setOverallVatPrice(detail.getOverallVatPrice());
            invoice.addChild(invoiceDetail);
        }
        invoice.setTotalPrice(inv.getTotalPrice());
        invoice.setTotalPriceInWord(inv.getTotalPriceInWord());
        invoice.setTotalSellPrice(inv.getTotalSellPrice());
        invoice.setTotalSellPriceInWord(inv.getTotalSellPriceInWord());
        invoice.setTotalVatPrice(inv.getTotalVatPrice());
        invoice.setTotalVatPriceInWord(inv.getTotalVatPriceInWord());
        invoice.setTotalProductCount(inv.getTotalProductCount());
        invoice.setStatus(ContractStatus.ACCEPTED);
        invoice = save(invoice);

        return invoice;
    }

    private void changeBalance(ServiceOrderInvoice invoice, String tin, String oldInvoiceNumber) {
        Contract contract = contractService.findById(invoice.getContractId());

        LedgerInvoiceRequestDto requestDto = new LedgerInvoiceRequestDto(
                invoice.getId(), contract.getName(), invoice.getId().toString(),
                contract.getName(), ServiceType.SERVICE, invoice.getTotalSellPrice(),
                invoice.getInvoiceDate().toLocalDate(),
                "/api/rent-invoice/" + invoice.getId() + "/generateTemplate",
                invoice.createDescriptionForLedger(tin, contract, oldInvoiceNumber)
        );

        Customer customer = contract.getCustomer();
        requestDto.fillCustomerData(customer);

        Company company = contract.getCompany();
        requestDto.fillCompanyData(company);

        ledgerService.payForOrder(requestDto);
    }
}
