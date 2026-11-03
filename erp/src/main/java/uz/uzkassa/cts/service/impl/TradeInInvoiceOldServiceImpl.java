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
import uz.uzkassa.cts.dto.tradeininvoice.response.TradeInInvoiceResponseDto;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.ServiceType;
import uz.uzkassa.cts.enums.XfileStatus;
import uz.uzkassa.cts.filter.TradeInInvoiceFilter;
import uz.uzkassa.cts.mappings.TradeInInvoiceMapper;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqXfileProducer;
import uz.uzkassa.cts.repository.TradeInInvoiceRepository;
import uz.uzkassa.cts.service.LedgerService;
import uz.uzkassa.cts.service.ThymeleafService;
import uz.uzkassa.cts.service.TradeInInvoiceOldService;
import uz.uzkassa.cts.service.XfileService;
import uz.uzkassa.cts.utils.InvoiceCreateExcelUtil;

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
public class TradeInInvoiceOldServiceImpl extends BaseServiceImpl<TradeInInvoice, Long> implements TradeInInvoiceOldService {

    private final TradeInInvoiceRepository repository;
    private final ThymeleafService thymeleafService;
    private final LedgerService ledgerService;
    private final XfileService xfileService;
    private final RabbitMqXfileProducer xfileProducer;
    private final TradeInInvoiceMapper tradeInInvoiceMapper;

    public TradeInInvoiceOldServiceImpl(TradeInInvoiceRepository repository,
                                        ThymeleafService thymeleafService,
                                        LedgerService ledgerService,
                                        XfileService xfileService,
                                        RabbitMqXfileProducer xfileProducer,
                                        TradeInInvoiceMapper tradeInInvoiceMapper) {
        super(repository);
        this.repository = repository;
        this.thymeleafService = thymeleafService;
        this.ledgerService = ledgerService;
        this.xfileService = xfileService;
        this.xfileProducer = xfileProducer;
        this.tradeInInvoiceMapper = tradeInInvoiceMapper;
    }

    @Override
    @Transactional
    public TradeInInvoice createInvoice(TradeInAct act) {
        TradeInInvoice invoice = new TradeInInvoice();
        invoice.setTotalSellPrice(act.getTotalPriceWithVat());
        invoice.setTotalPrice(act.getPriceFor1pc());
        invoice.setTotalVatPrice(act.getVat1pc());
        invoice.setPriceFor1pc(act.getPriceFor1pc());
        invoice.setStatus(ContractStatus.ACCEPTED);

        TradeInInvoiceDetail detail = new TradeInInvoiceDetail();
        detail.setInvoice(invoice);
        detail.setTotalProductCount(act.getTotalProductCount());
        detail.setPriceFor1pc(act.getPriceFor1pc());
        detail.setVatFor1pc(act.getVat1pc());
        detail.setTotalSellPrice(act.getTotalPriceWithVat());
        detail.setTotalVatPrice(act.getVat1pc().multiply(BigDecimal.valueOf(act.getTotalProductCount())));
        detail.setDeviceSerialNumber(act.getDevice().getSerialNumber());
        detail.setServiceName(act.getDevice().getModel().getName());
        TradeInPriceHistory history = act.getContract().getTradeInPriceHistory();
        detail.setCode(history.getCode());
        detail.setPackageCode(history.getPackageCode());
        detail.setPackageName(history.getPackageName());
        detail.setCatalogCode(history.getCatalogCode());
        detail.setCatalogName(history.getCatalogName());
        detail.setOrigin(history.getOrigin());
        invoice.setDetail(detail);


        invoice.setContract(act.getContract());
        invoice.setTradeInAct(act);
        act.setTradeInInvoice(invoice);
        save(invoice);

        changeBalance(invoice);

        return invoice;
    }

    @Override
    public Page<TradeInInvoiceResponseDto> list(TradeInInvoiceFilter filter) {
        Specification<TradeInInvoice> specification = getTradeInInvoiceSpecification(filter);
        return repository.findAll(specification, filter.getOrderedPageable())
                .map(tradeInInvoiceMapper::toDto);
    }

    private Specification<TradeInInvoice> getTradeInInvoiceSpecification(TradeInInvoiceFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            ArrayList<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Join<TradeInContract, TradeInInvoice> contractJoin = root.join(TradeInInvoice_.CONTRACT);
                Join<Customer, TradeInContract> customerJoin = contractJoin.join(TradeInContract_.CUSTOMER);
                Path<String> tinPath = customerJoin.get(Customer_.TIN);
                Path<String> customerNamePath = customerJoin.get(Customer_.NAME);
                Predicate customerNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerNamePath), filter.getSearchForQuery());
                Predicate tinPred = criteriaBuilder.like(criteriaBuilder.lower(tinPath), filter.getSearchForQuery());
                list.add(criteriaBuilder.or(customerNamePred, tinPred));
            }
            if (filter.getId() != null) {
                Path<String> id = root.get(TradeInInvoice_.ID);
                list.add(criteriaBuilder.equal(id, filter.getId()));
            }
            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get(TradeInInvoice_.INVOICE_DATE);
                list.add(criteriaBuilder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }
            if (filter.getStatus() != null) {
                Path<ContractStatus> status = root.get(TradeInInvoice_.STATUS);
                list.add(criteriaBuilder.equal(status, ContractStatus.valueOf(filter.getStatus())));
            }
            if (filter.getXfileStatus() != null) {
                Path<XfileStatus> xfileStatus = root.get(TradeInInvoice_.XFILE_STATUS);
                if (XfileStatus.NOT_SEND.equals(filter.getXfileStatus())) {
                    Path<String> xfileId = root.get(TradeInInvoice_.XFILE_ID);
                    list.add(criteriaBuilder.isNull(xfileId));
                } else {
                    list.add(criteriaBuilder.equal(xfileStatus, filter.getXfileStatus()));
                }
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
    }

    @Override
    public TradeInInvoiceResponseDto getById(Long id) {
        return tradeInInvoiceMapper.toDto(findById(id));
    }


    @Override
    public String getHtmlTemplate(Long id) {
        return thymeleafService.generateTradeInInvoice(findById(id));
    }

    @Override
    public TradeInInvoiceResponseDto getByContractId(Long contractId) {
        return tradeInInvoiceMapper.toDto(repository.findByContract_Id(contractId));
    }

    @Override
    public void revertInvoice(Long id) {
        //Hozircha commentga olib qo`yamiz manimcha bu funktsiya kerak bo`lmaydi
        //Kerak bo`lsa commentdan ochish kerak va ishlaydi
//        TradeInInvoice invoice = findById(id);
//        if (invoice.getStatus() != ContractStatus.ACCEPTED)
//            throw new BadRequestExp("you.can.not.rejected", invoice.getStatus().getNameUzCyrillic());
//
//        //revert balance
//        ledgerService.revertByInvoiceId(invoice.getId(), InvoiceType.TRADE_IN_INVOICE);
//        //invoice.setDeleted(true);
//        invoice.setStatus(ContractStatus.REJECTED);
//        save(invoice);
//        //SWITCH DEVICE
//        rabbitMqDeviceSwitcherProducer.deviceStatusSwitch(invoice.getContract().getCustomer().getTin());
    }

    @Override
    @Transactional
    public void resendToXfileWithNewDate(ResendToXfileRequest dto) {
        TradeInInvoice invoice = findById(dto.getId());
        invoice.setInvoiceDate(dto.getDate().atStartOfDay());
        String xfileId = xfileService.sendInvoice(InvoiceToFacturaMapping.mapInvoiceToFactura(invoice));
        invoice.setXfileId(xfileId);
        save(invoice);
    }

    @Override
    public TradeInInvoiceResponseDto checkInvoiceXfileStatus(Long id) {
        TradeInInvoice invoice = findById(id);
        if (StringUtils.isNotEmpty(invoice.getXfileId())) {
            FacturaCheckStatusDataDto dto = xfileService.checkFacturaStatus(invoice.getXfileId());
            if (!dto.getStatus().equals(invoice.getXfileStatus())) {
                invoice.setXfileStatus(dto.getStatus());
                save(invoice);
            }
        }
        return tradeInInvoiceMapper.toDto(invoice);
    }

    @Override
    public byte[] download(LocalDate from, LocalDate to, ContractStatus status) {
        List<TradeInInvoice> invoices = repository.findAll(getTradeInInvoiceSpecification(new TradeInInvoiceFilter(from, to, status)));

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("TradeInInvoice");
            String[] headerNames = new String[]{"ИНН", "Название компании", "Регион", "Адрес", "Номер телефона",
                    "МФО", "Расчётный счёт", "Номер с/ф", "Дата с/ф", "Сумма", "Статус"};
            InvoiceCreateExcelUtil.createHeader(headerNames, sheet);
            createBody(invoices, sheet);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(EXCEL_FILE_CREATION_FAILED, e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<TradeInInvoice> findInvoicesToSendXfile() {
        return repository.findAllByXfileIdIsNull();
    }

    @Override
    public void updateInvoiceXfileStatuses() {
        for (TradeInInvoice invoice : repository.updateInvoiceXfileStatuses()) {
            xfileProducer.updateTradeInInvoice(invoice.getId());
        }
    }

    public void createBody(List<TradeInInvoice> invoices, XSSFSheet sheet) {
        int rowCount = 1;
        for (TradeInInvoice invoice : invoices) {
            TradeInContract contract = invoice.getContract();
            Customer customer = contract.getCustomer();
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

    private void changeBalance(TradeInInvoice invoice) {
        LedgerInvoiceRequestDto requestDto = new LedgerInvoiceRequestDto();

        TradeInContract contract = invoice.getContract();
        requestDto.setId(invoice.getId());
        requestDto.setDocumentNo(contract.getId().toString());
        requestDto.setTransactionNumber(invoice.getId().toString());
        requestDto.setContractNumber(contract.getNumber());
        requestDto.setServiceType(ServiceType.SERVICE);
        requestDto.setAmount(invoice.getTotalSellPrice());
        requestDto.setPaymentDate(invoice.getCreatedDate().toLocalDate());
        requestDto.setLink("/api/trade-in-invoice/" + invoice.getId() + "/generateTemplate");

        Customer customer = contract.getCustomer();
        //Bank customerBank = customer.getBank();
        CustomerBank customerBank = customer.getCustomerBank();
        if (customerBank == null) {
            log.error("Customer Bank not fount Customer tin {}", customer.getTin());
            throw validationService.entityNotFoundExceptionThrow(BANK, TIN, customer.getTin()).get();
        }
        requestDto.setPayerAccountNumber(customerBank.getAccountNumber());
        requestDto.setPayerBankName(customerBank.getBank().getNameNotNullable());
        requestDto.setPayerBankCode(customerBank.getBank().getCode());
        requestDto.setPayerIdentityNumber(customer.getTin());
        requestDto.setPayerName(customer.getName() == null ? "" : customer.getName());

        Company company = contract.getCompany();
        CompanyBank companyBank = company.getCompanyBank();
        requestDto.setReceiverAccountNumber(companyBank.getAccountNumber());
        requestDto.setPayerBankName(companyBank.getBank().getNameNotNullable());
        requestDto.setReceiverIdentityNumber(company.getTin());
        requestDto.setReceiverName(company.getName() == null ? "" : company.getName());
        requestDto.setReceiverBankCode(companyBank.getBank().getCode());

        TradeInInvoiceDetail detail = invoice.getDetail();

        String builder = " Договор № " +
                contract.getNumber() +
                " от " + contract.getCreatedDate() + " г." +
                " Общ.сумма " + invoice.getTotalSellPrice() +
                " за покупку: " + detail.getServiceName() + " " + detail.getDeviceSerialNumber();
        requestDto.setDescription(builder);

        ledgerService.payForOrder(requestDto);
    }
}
