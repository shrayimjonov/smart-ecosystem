package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
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
import uz.uzkassa.cts.dto.sale.invoice.SaleInvoiceExcelExportDto;
import uz.uzkassa.cts.dto.sale.invoice.SaleInvoiceResponseDto;
import uz.uzkassa.cts.dto.vat.VatInvoiceCalculationDto;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.ServiceType;
import uz.uzkassa.cts.filter.TradeInInvoiceFilter;
import uz.uzkassa.cts.helper.excelhelper.XlsxWriter;
import uz.uzkassa.cts.mappings.SaleInvoiceMapper;
import uz.uzkassa.cts.repository.SaleInvoiceRepository;
import uz.uzkassa.cts.service.*;
import uz.uzkassa.cts.utils.CalcUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SaleInvoiceOldServiceImpl extends BaseServiceImpl<SaleInvoice, Long> implements SaleInvoiceOldService {

    private final SaleInvoiceRepository repository;
    private final ThymeleafService thymeleafService;
    private final LedgerService ledgerService;
    private final XfileService xfileService;
    private final SaleAccessoryService saleAccessoryService;
    private final VatService vatService;
    private final XlsxWriter xlsxWriter;
    private final SaleInvoiceMapper saleInvoiceMapper;

    public SaleInvoiceOldServiceImpl(SaleInvoiceRepository repository,
                                     ThymeleafService thymeleafService,
                                     LedgerService ledgerService,
                                     XfileService xfileService,
                                     SaleAccessoryService saleAccessoryService,
                                     VatService vatService, XlsxWriter xlsxWriter,
                                     SaleInvoiceMapper saleInvoiceMapper) {
        super(repository);
        this.repository = repository;
        this.thymeleafService = thymeleafService;
        this.ledgerService = ledgerService;
        this.xfileService = xfileService;
        this.saleAccessoryService = saleAccessoryService;
        this.vatService = vatService;
        this.xlsxWriter = xlsxWriter;
        this.saleInvoiceMapper = saleInvoiceMapper;
    }

    @Override
    @Transactional
    public SaleInvoice createInvoice(SaleAct saleAct) {
        SaleInvoice invoice = new SaleInvoice();
        Vat vat = vatService.getActiveVat();
        List<SaleInvoiceDetail> detailList = new ArrayList<>();
        //if (optionalVat.isPresent()) {
        Integer sumQty = 0;
        BigDecimal sumTotalPrice = BigDecimal.ZERO;
        BigDecimal sumTotalVatPrice = BigDecimal.ZERO;
        BigDecimal sumPriceFor1pc = BigDecimal.ZERO;
        BigDecimal sumTotalSellPrice = BigDecimal.ZERO;
        for (SaleAccessory saleAccessory : saleAccessoryService.getByActId(saleAct.getId())) {
            Accessory accessory = saleAccessory.getAccessory();
            VatInvoiceCalculationDto calc = CalcUtils.calculateInvoice(saleAccessory.getAccessory().getPrice(), 1, vatService.getActiveVat().getPercent());

            BigDecimal qtyPrice = calc.getTotalPriceWithVat().multiply(BigDecimal.valueOf(saleAccessory.getQty()));
            BigDecimal priceVat = calc.getTotalVat();

            SaleInvoiceDetail detail = new SaleInvoiceDetail();
            detail.setSaleInvoice(invoice);
            detail.setTotalProductCount(saleAccessory.getQty());
            detail.setPriceFor1pc(calc.getTotalPrice());
            detail.setVatFor1pc(calc.getTotalVat());
            detail.setTotalSellPrice(qtyPrice);
            detail.setTotalVatPrice(priceVat.multiply(BigDecimal.valueOf(saleAccessory.getQty())));
            detail.setAccessoryName(accessory.getName());
            detail.setServiceName(accessory.getModel().getName());

            sumQty += saleAccessory.getQty();
            sumTotalPrice = sumTotalPrice.add(calc.getTotalPrice().multiply(BigDecimal.valueOf(saleAccessory.getQty())));
            sumTotalVatPrice = sumTotalVatPrice.add(priceVat.multiply(BigDecimal.valueOf(saleAccessory.getQty())));
            sumPriceFor1pc = sumPriceFor1pc.add(calc.getTotalPrice());
            sumTotalSellPrice = sumTotalSellPrice.add(qtyPrice);

            detail.setCode(accessory.getCode());
            detail.setPackageCode(accessory.getPackageCode());
            detail.setPackageName(accessory.getPackageName());
            detail.setCatalogCode(accessory.getCatalogCode());
            detail.setCatalogName(accessory.getCatalogName());
            detail.setOrigin(accessory.getOrigin());
            detailList.add(detail);
        }

        invoice.setTotalProductCount(sumQty);
        invoice.setTotalPrice(sumTotalPrice);
        invoice.setTotalVatPrice(sumTotalVatPrice);
        invoice.setPriceFor1pc(sumPriceFor1pc);
        invoice.setTotalSellPrice(sumTotalSellPrice);
        invoice.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
        invoice.setDetails(detailList);
        invoice.setSaleContract(saleAct.getSaleContract());
        invoice.setSaleAct(saleAct);
        saleAct.setSaleInvoice(invoice);
        save(invoice);
        //}
        changeBalance(invoice);

        return invoice;
    }

    @Override
    public Page<SaleInvoiceResponseDto> list(TradeInInvoiceFilter filter) {
        Specification<SaleInvoice> specification = getSaleInvoiceSpecification(filter);
        return repository.findAll(specification, filter.getOrderedPageable())
                .map(saleInvoiceMapper::toDto);
    }

    private Specification<SaleInvoice> getSaleInvoiceSpecification(TradeInInvoiceFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            ArrayList<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Join<SaleContract, SaleInvoice> contractJoin = root.join(SaleInvoice_.SALE_CONTRACT);
                Join<Customer, SaleContract> customerJoin = contractJoin.join(SaleContract_.CUSTOMER);
                Path<String> tinPath = customerJoin.get(Customer_.TIN);
                Path<String> customerNamePath = customerJoin.get(Customer_.NAME);
                Predicate customerNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerNamePath), filter.getSearchForQuery());
                Predicate tinPred = criteriaBuilder.like(criteriaBuilder.lower(tinPath), filter.getSearchForQuery());
                list.add(criteriaBuilder.or(customerNamePred, tinPred));
            }
            if (filter.getId() != null) {
                Path<String> id = root.get(SaleInvoice_.ID);
                list.add(criteriaBuilder.equal(id, filter.getId()));
            }
            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get(SaleInvoice_.INVOICE_DATE);
                list.add(criteriaBuilder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }
            if (filter.getStatus() != null) {
                Path<ContractStatus> status = root.get(SaleInvoice_.STATUS);
                list.add(criteriaBuilder.equal(status, ContractStatus.valueOf(filter.getStatus())));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
    }

    @Override
    public SaleInvoiceResponseDto getById(Long id) {
        return saleInvoiceMapper.toDto(findById(id));
    }

    @Override
    public String getHtmlTemplate(Long id) {
        return thymeleafService.generateSaleInvoice(findById(id));
    }

    @Override
    public SaleInvoiceResponseDto getBySaleContractId(Long saleContractId) {
        return saleInvoiceMapper.toDto(repository.findBySaleContract_Id(saleContractId));
    }

    @Override
    @Transactional
    public void resendToXfileWithNewDate(ResendToXfileRequest dto) {
        SaleInvoice invoice = findById(dto.getId());
        invoice.setInvoiceDate(dto.getDate().atStartOfDay());
        String xfileId = xfileService.sendInvoice(InvoiceToFacturaMapping.mapSaleInvoiceToFactura(invoice));
        invoice.setXfileId(xfileId);
        save(invoice);
    }

    @Override
    public SaleInvoiceResponseDto checkInvoiceXfileStatus(Long id) {
        SaleInvoice invoice = findById(id);
        if (StringUtils.isNotEmpty(invoice.getXfileId())) {
            FacturaCheckStatusDataDto dto = xfileService.checkFacturaStatus(invoice.getXfileId());
            if (!dto.getStatus().equals(invoice.getXfileStatus())) {
                invoice.setXfileStatus(dto.getStatus());
                save(invoice);
            }
        }
        return saleInvoiceMapper.toDto(invoice);
    }

    @Override
    public byte[] download(LocalDate from, LocalDate to, ContractStatus status) {
        List<SaleInvoiceExcelExportDto> invoicesDTO = repository.findAll(getSaleInvoiceSpecification(new TradeInInvoiceFilter(from, to, status)))
                .stream().map(saleInvoice -> {
                    SaleContract saleContract = saleInvoice.getSaleContract();
                    Customer customer = saleContract.getCustomer();
                    CustomerBank customerBank = customer.getCustomerBank();
                    BaseAddress baseAddress = customer.getAddress();

                    return SaleInvoiceExcelExportDto.builder()
                            .customerTin(customer.getTin())
                            .customerName(customer.getCompanyName())
                            .region(baseAddress.getRegion().getNameUzCyrillic())
                            .address(baseAddress.getFullAddressAddress())
                            .phone(customer.getPhone())
                            .bankCode(customerBank.getBank().getCode())
                            .accountNumber(customerBank.getAccountNumber())
                            .invoiceNumber(saleInvoice.getNumber())
                            .invoiceCreatedDate(saleInvoice.getCreatedDate().toString().substring(0, 10))
                            .totalSellPrice(saleInvoice.getTotalSellPrice().doubleValue())
                            .invoiceStatus(saleInvoice.getStatus().getName()).build();
                }).collect(Collectors.toList());

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            try (Workbook workbook = new XSSFWorkbook()) {
                xlsxWriter.write(invoicesDTO, bos, workbook);
            } catch (Exception e) {
                log.error("Generating Sale Invoice xls file failed", e);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            log.error("ByteArrayOutputStream ex --- ", e);
            return new byte[0];
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<SaleInvoice> findInvoicesToSendXfile() {
        return repository.findAllByXfileIdIsNull();
    }

    private void changeBalance(SaleInvoice saleInvoice) {
        LedgerInvoiceRequestDto requestDto = new LedgerInvoiceRequestDto();

        SaleContract saleContract = saleInvoice.getSaleContract();
        requestDto.setId(saleInvoice.getId());
        requestDto.setDocumentNo(saleContract.getId().toString());
        requestDto.setTransactionNumber(saleInvoice.getId().toString());
        requestDto.setContractNumber(saleContract.getNumber());
        requestDto.setServiceType(ServiceType.SERVICE);
        requestDto.setAmount(saleInvoice.getTotalSellPrice());
        requestDto.setPaymentDate(saleInvoice.getCreatedDate().toLocalDate());
        requestDto.setLink("/api/sale-invoice/" + saleInvoice.getId() + "/generateTemplate");

        Customer customer = saleContract.getCustomer();
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

        Company company = saleContract.getCompany();
        CompanyBank companyBank = company.getCompanyBank();
        requestDto.setReceiverAccountNumber(companyBank.getAccountNumber());
        requestDto.setPayerBankName(companyBank.getBank().getNameNotNullable());
        requestDto.setReceiverIdentityNumber(company.getTin());
        requestDto.setReceiverName(company.getName() == null ? "" : company.getName());
        requestDto.setReceiverBankCode(companyBank.getBank().getCode());

        int countDetail = 0;
        StringBuilder builder = new StringBuilder();
        for (SaleInvoiceDetail saleInvoiceDetail : saleInvoice.getDetails()) {
            builder.append("\n").append(countDetail++);
            builder.append(". Договор № ");
            builder.append(saleContract.getNumber());
            builder.append("  от ").append(saleContract.getCreatedDate()).append(" г.");
            builder.append("  Общ.сумма ").append(saleInvoice.getTotalSellPrice());
            builder.append("  за покупку: ").append(saleInvoiceDetail.getServiceName()).append(" ").append(saleInvoiceDetail.getAccessoryName());
        }
        requestDto.setDescription(builder.toString());
        ledgerService.payForOrder(requestDto);
    }
}
