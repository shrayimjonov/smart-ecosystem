package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.factura.FacturaCheckStatusDataDto;
import uz.uzkassa.cts.dto.invoice.mappings.InvoiceToFacturaMapping;
import uz.uzkassa.cts.dto.invoice.request.LedgerInvoiceRequestDto;
import uz.uzkassa.cts.dto.invoice.request.ResendToXfileRequest;
import uz.uzkassa.cts.dto.onetimeorderinvoice.response.OneTimeOrderInvoiceResponseDto;
import uz.uzkassa.cts.dto.serviceorderinvoice.response.ServiceOrderInvoiceListDto;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.dto.vat.VatInvoiceCalculationDto;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.OneTimeOrderInvoiceFilter;
import uz.uzkassa.cts.mappings.OneTimeOrderInvoiceMapper;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqDeviceSwitcherProducer;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqXfileProducer;
import uz.uzkassa.cts.repository.OneTimeOrderInvoiceRepository;
import uz.uzkassa.cts.repository.OneTimeOrderRepository;
import uz.uzkassa.cts.service.*;
import uz.uzkassa.cts.utils.BigDecimalUtil;
import uz.uzkassa.cts.utils.CalcUtils;
import uz.uzkassa.cts.utils.InvoiceCreateExcelUtil;
import uz.uzkassa.cts.utils.NumberToWordUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
public class OneTimeOrderInvoiceOldServiceImpl extends BaseServiceImpl<OneTimeOrderInvoice, Long> implements OneTimeOrderInvoiceOldService {

    private final OneTimeOrderInvoiceRepository repository;
    private final OneTimeOrderRepository oneTimeOrderRepository;
    private final LedgerService ledgerService;
    private final ContractService contractService;
    private final FileUploadService fileUploadService;
    private final ThymeleafService thymeleafService;
    private final RabbitMqDeviceSwitcherProducer rabbitMqProducer;
    private final RabbitMqXfileProducer xfileProducer;
    private final CtsServiceService ctsServiceService;
    private final XfileService xfileService;
    private final VatService vatService;
    private final OneTimeOrderInvoiceMapper oneTimeOrderInvoiceMapper;

    public OneTimeOrderInvoiceOldServiceImpl(OneTimeOrderInvoiceRepository repository,
                                             OneTimeOrderRepository oneTimeOrderRepository,
                                             LedgerService ledgerService,
                                             ContractService contractService,
                                             FileUploadService fileUploadService,
                                             ThymeleafService thymeleafService,
                                             RabbitMqDeviceSwitcherProducer rabbitMqProducer,
                                             RabbitMqXfileProducer xfileProducer,
                                             CtsServiceService ctsServiceService,
                                             XfileService xfileService,
                                             VatService vatService,
                                             OneTimeOrderInvoiceMapper oneTimeOrderInvoiceMapper) {
        super(repository);
        this.repository = repository;
        this.oneTimeOrderRepository = oneTimeOrderRepository;
        this.ledgerService = ledgerService;
        this.contractService = contractService;
        this.fileUploadService = fileUploadService;
        this.thymeleafService = thymeleafService;
        this.rabbitMqProducer = rabbitMqProducer;
        this.xfileProducer = xfileProducer;
        this.ctsServiceService = ctsServiceService;
        this.xfileService = xfileService;
        this.vatService = vatService;
        this.oneTimeOrderInvoiceMapper = oneTimeOrderInvoiceMapper;
    }

    @Override
    public void createInvoice() {
        List<OneTimeOrder> orders = oneTimeOrderRepository.findByInvoiceIdIsNullAndStatusOrderByIdDesc(ContractStatus.ACCEPTED);
        for (OneTimeOrder order : orders) {
            OneTimeOrderInvoice invoice = create(order);
            order.setInvoice(invoice);
        }
    }

    @Override
    public Page<OneTimeOrderInvoiceResponseDto> list(OneTimeOrderInvoiceFilter filter) {

        Specification<OneTimeOrderInvoice> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            Path<Customer> customer = root.join(OneTimeOrderInvoice_.CUSTOMER, JoinType.INNER);
            ArrayList<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Path<String> tin = customer.get(Customer_.TIN);
                Path<String> customerName = customer.get(Customer_.NAME);
                Path<String> customerCompanyName = customer.get(Customer_.COMPANY_NAME);

                Predicate customerNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerName), filter.getSearchForQuery());
                Predicate customerCompanyNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerCompanyName), filter.getSearchForQuery());
                Predicate tinPred = criteriaBuilder.like(criteriaBuilder.lower(tin), filter.getSearchForQuery());
                list.add(criteriaBuilder.or(customerNamePred, tinPred, customerCompanyNamePred));
            }
            if (filter.getId() != null) {
                Path<Long> idPath = customer.get(OneTimeOrderInvoice_.ID);
                list.add(criteriaBuilder.equal(idPath, filter.getId()));
            }
            if (filter.getServiceId() != null) {
                Path<Long> serviceIdPath = root.get(OneTimeOrderInvoice_.SERVICE_ID);
                list.add(criteriaBuilder.equal(serviceIdPath, filter.getServiceId()));
            }
            if (filter.getContractStatus() != null) {
                Path<ContractStatus> orderStatus = root.get(OneTimeOrderInvoice_.STATUS);
                list.add(criteriaBuilder.equal(orderStatus, filter.getContractStatus()));
            }
            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get(OneTimeOrderInvoice_.CREATED_DATE);
                list.add(criteriaBuilder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }
            if (filter.getXfileStatus() != null) {
                Path<XfileStatus> xfileStatus = root.get(OneTimeOrderInvoice_.XFILE_STATUS);
                if (XfileStatus.NOT_SEND.equals(filter.getXfileStatus())) {
                    Path<String> xfileId = root.get(OneTimeOrderInvoice_.X_FILE_ID);
                    list.add(criteriaBuilder.isNull(xfileId));
                } else {
                    list.add(criteriaBuilder.equal(xfileStatus, filter.getXfileStatus()));
                }
            }

            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        Page<OneTimeOrderInvoice> orders = repository.findAll(specification, filter.getOrderedPageable());

        return orders.map(oneTimeOrderInvoiceMapper::toDto);
    }

    @Override
    public OneTimeOrderInvoiceResponseDto getById(Long id) {
        OneTimeOrderInvoice invoice = findById(id);
        return oneTimeOrderInvoiceMapper.toDto(invoice);
    }

    @Override
    public OneTimeOrderInvoiceResponseDto uploadFile(Long id, MultipartFile file) {
        OneTimeOrderInvoice invoice = findById(id);
        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.ONE_TIME_ORDER_SERVICE.name(), DocumentType.INVOICE.name());

        invoice.setFileName(response.getFileName());
        invoice.setContentType(file.getContentType());
        invoice.setFileUri(response.getFileDownloadUri());
        invoice.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        // update invoice with file
        invoice = save(invoice);

        return oneTimeOrderInvoiceMapper.toDto(invoice);
    }

    @Override
    public String getSpecificationHtmlTemplate(Long id) {
        OneTimeOrderInvoice invoice = findById(id);
        return thymeleafService.generateOneTimeOrderInvoiceHTML(invoice);
    }

    @Override
    public Page<OneTimeOrderInvoiceResponseDto> getByContractId(Long contractId, BaseFilter filter) {

        Page<OneTimeOrderInvoice> invoices = repository.findByContractIdOrderByIdDesc(contractId, filter.getOrderedPageable());

        return invoices.map(oneTimeOrderInvoiceMapper::toDto);
    }

    @Override
    @Transactional
    public void revertInvoice(Long id) {
        OneTimeOrderInvoice invoice = findById(id);
        if (invoice.getStatus() != ContractStatus.ACCEPTED)
            throw validationService.statusChangeExceptionThrow(invoice.getStatus().getName()).get();

        //revert balance
        ledgerService.revertByInvoiceId(invoice.getId(), InvoiceType.ONE_TIME_ORDER_INVOICE);
        //invoice.setDeleted(true);
        invoice.setStatus(ContractStatus.REJECTED);
        save(invoice);

        OneTimeOrder order = invoice.getOrder();
        order.setStatus(ContractStatus.REJECTED);
        oneTimeOrderRepository.save(order);
        //SWITCH DEVICE
        rabbitMqProducer.deviceStatusSwitch(invoice.getCustomer().getTin());
    }

    @Override
    public String getInvoicesDownloadUrl(LocalDate from, LocalDate to, ContractStatus status) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Invoices");
            String[] headerNames = new String[]{"Номер", "ИНН", "Название компании",
                "Договор", "Дата дог.", "Счет фак.", "Дата сч/ф.", "Название сервиса",
                "Стоимость поставки", "НДС Сумм", "Стоим. пост. c НДС", "Статус",
                "мфо", "расчетный счет", "Банк"};
            InvoiceCreateExcelUtil.createHeader(headerNames, sheet);
            List<ServiceOrderInvoiceListDto> invoiceListDtos = getInvoices(from, to, status);
            InvoiceCreateExcelUtil.createBody(invoiceListDtos, sheet, InvoiceType.ONE_TIME_ORDER_INVOICE);


            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            byte[] bytes = bos.toByteArray();
            return fileUploadService.upload(bytes,
                    "invoices.xls",
                    "application/vnd.ms-excel",
                    (long) bos.size(), 0L,
                    EntityTypeEnum.ONE_TIME_ORDER_SERVICE.name(),
                    DocumentType.ONE_TIME_ORDER.name())
                .getFileDownloadUri();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public List<OneTimeOrderInvoice> getInvoicesForXfile(LocalDate from, LocalDate to, ContractStatus status) {
        return repository.getInvoicesForXfile(from, to, status);
    }

    @Override
    public void resendInvoiceToXfileWithNewDate(ResendToXfileRequest dto) {
        OneTimeOrderInvoice invoice = findById(dto.getId());
        if (invoice.getForXfile() != null && !invoice.getForXfile())
            throw validationService.badRequestExceptionThrow(INVOICE_NOT_SENT_XFILE).get();
        invoice.setInvoiceDate(dto.getDate().atStartOfDay());
        save(invoice);
        xfileProducer.sendOneTimeOrderInvoice(InvoiceToFacturaMapping.mapInvoiceToFactura(invoice));
    }

    @Override
    public OneTimeOrderInvoiceResponseDto checkInvoiceXfileStatus(Long id) {
        OneTimeOrderInvoice invoice = findById(id);
        if (StringUtils.isNotEmpty(invoice.getXFileId())) {
            FacturaCheckStatusDataDto dto = xfileService.checkFacturaStatus(invoice.getXFileId());
            if (!dto.getStatus().equals(invoice.getXfileStatus())) {
                invoice.setXfileStatus(dto.getStatus());
                save(invoice);
            }
        }
        return oneTimeOrderInvoiceMapper.toDto(invoice);
    }

    @Override
    public void updateInvoiceXfileStatuses() {
        for (OneTimeOrderInvoice invoice : repository.updateInvoiceXfileStatuses()) {
            xfileProducer.updateOneTimeInvoice(invoice.getId());
        }
    }

    private List<ServiceOrderInvoiceListDto> getInvoices(LocalDate from, LocalDate to, ContractStatus status) {

        return repository.getInvoices(from, to, status);
    }

    public void recalculate(Long invoiceId) {
        OneTimeOrderInvoice invoice = findById(invoiceId);

        VatInvoiceCalculationDto calc = CalcUtils.calculateInvoice(invoice.getTotalSellPrice(), invoice.getTotalProductCount(),vatService.getActiveVat().getPercent());
        //invoice.setTotalSellPrice(calc.getTotalPriceWithVat());
        invoice.setTotalPrice(calc.getTotalPrice());
        invoice.setTotalPriceInWord(NumberToWordUtil.digits2Text(BigDecimalUtil.setScaleByTwo(calc.getTotalPrice())));
        invoice.setTotalVatPrice(calc.getTotalVat());
        invoice.setTotalVatPriceInWord(NumberToWordUtil.digits2Text(BigDecimalUtil.setScaleByTwo(calc.getTotalPrice())));
        //invoice.setPrice1pc(invoiceDetail.getTariff().getPrice());
        save(invoice);
    }

    private OneTimeOrderInvoice create(OneTimeOrder order) {
        OneTimeOrderInvoice invoice = new OneTimeOrderInvoice();
        invoice.setOrder(order);
        invoice.setTotalProductCount(order.getTotalProductCount());
        invoice.setStatus(ContractStatus.ACCEPTED);

        BigDecimal vatPriceFor1 = BigDecimalUtil.calculateVatPriceFor1(order.getTotalSellPrice(), vatService.getActiveVat().getPercent());
        BigDecimal productPriceFor1 = BigDecimalUtil.calculateProductPriceFor1SubtractVatPrice(order.getTotalSellPrice(), vatPriceFor1);

        invoice.setTotalPriceInWord(NumberToWordUtil.digits2Text(BigDecimalUtil.setScaleByTwo(productPriceFor1)));
        invoice.setTotalPrice(productPriceFor1);
        invoice.setTotalVatPriceInWord(NumberToWordUtil.digits2Text(BigDecimalUtil.setScaleByTwo(vatPriceFor1)));
        invoice.setTotalVatPrice(vatPriceFor1);
        invoice.setTotalSellPrice(order.getTotalSellPrice());
        invoice.setTotalSellPriceInWord(order.getTotalSellPriceInWord());

        CtsService service = ctsServiceService.findById(order.getServiceId());
        invoice.setForXfile(service.getForXfile());
        invoice.setServiceId(service.getId());
        invoice.setService(service);
        invoice.setCustomerId(order.getCustomerId());
        invoice.setCustomer(order.getCustomer());
        invoice.setContractId(order.getContractId());
        invoice.setContract(order.getContract());
        invoice = save(invoice);
        //Change balance
        changeBalance(invoice);
        //SWITCH DEVICE
        rabbitMqProducer.deviceStatusSwitch(order.getCustomer().getTin());
        return invoice;
    }

    private void changeBalance(OneTimeOrderInvoice invoice) {
        Contract contract = contractService.findById(invoice.getContractId());
        Customer customer = contract.getCustomer();
        Company company = contract.getCompany();

        LedgerInvoiceRequestDto requestDto = new LedgerInvoiceRequestDto()
            .id(invoice.getId())
            .documentNo(contract.getName())
            .transactionNumber(invoice.getId().toString())
            .contractNumber(contract.getName())
            .serviceType(ServiceType.SERVICE)
            .amount(invoice.getTotalSellPrice())
            .paymentDate(invoice.getCreatedDate().toLocalDate())
            .link("/api/one-time-order-invoice/" + invoice.getId() + "/generateTemplate")
            .description(invoice.createDescription())
            .company(company)
            .customer(customer);
        ledgerService.payForOrder(requestDto);
    }
}
