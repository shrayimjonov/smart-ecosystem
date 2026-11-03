package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.actoftransmissioninvoice.response.ActOfTransmissionInvoiceListResponseDto;
import uz.uzkassa.cts.dto.actoftransmissioninvoice.response.ActOfTransmissionInvoiceResponseDto;
import uz.uzkassa.cts.dto.factura.FacturaCheckStatusDataDto;
import uz.uzkassa.cts.dto.invoice.mappings.InvoiceToFacturaMapping;
import uz.uzkassa.cts.dto.invoice.request.ResendToXfileRequest;
import uz.uzkassa.cts.dto.serviceorderinvoice.response.ServiceOrderInvoiceListDto;
import uz.uzkassa.cts.dto.vat.VatInvoiceCalculationDto;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.DocumentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.InvoiceType;
import uz.uzkassa.cts.filter.PaymentScheduleInvoiceFilter;
import uz.uzkassa.cts.mappings.ActOfTransmissionInvoiceMapper;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqXfileProducer;
import uz.uzkassa.cts.repository.ActOfTransmissionInvoiceRepository;
import uz.uzkassa.cts.service.*;
import uz.uzkassa.cts.utils.CalcUtils;
import uz.uzkassa.cts.utils.InvoiceCreateExcelUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class ActOfTransmissionInvoiceOldServiceImpl extends BaseServiceImpl<ActOfTransmissionInvoice, Long> implements ActOfTransmissionInvoiceOldService {

    private final ActOfTransmissionInvoiceRepository repository;
    private final ThymeleafService thymeleafService;
    private final ActOfTransmissionStrategyService actOfTransmissionService;
    private final RabbitMqXfileProducer xfileProducer;
    private final FileUploadService fileUploadService;
    private final XfileService xfileService;
    private final VatService vatService;
    private final ActOfTransmissionInvoiceMapper actOfTransmissionInvoiceMapper;

    public ActOfTransmissionInvoiceOldServiceImpl(ActOfTransmissionInvoiceRepository repository,
                                                  ThymeleafService thymeleafService,
                                                  ActOfTransmissionStrategyService actOfTransmissionService,
                                                  RabbitMqXfileProducer xfileProducer,
                                                  FileUploadService fileUploadService,
                                                  XfileService xfileService,
                                                  VatService vatService,
                                                  ActOfTransmissionInvoiceMapper actOfTransmissionInvoiceMapper) {
        super(repository);
        this.repository = repository;
        this.thymeleafService = thymeleafService;
        this.actOfTransmissionService = actOfTransmissionService;
        this.xfileProducer = xfileProducer;
        this.fileUploadService = fileUploadService;
        this.xfileService = xfileService;
        this.vatService = vatService;
        this.actOfTransmissionInvoiceMapper = actOfTransmissionInvoiceMapper;
    }

    @Override
    public ActOfTransmissionInvoiceResponseDto getByActOfTransmissionId(Long id) {
        ActOfTransmissionInvoice invoice = repository.findByActOfTransmission_id(id);
        return actOfTransmissionInvoiceMapper.toDto(invoice);
    }

    @Override
    public Page<ActOfTransmissionInvoiceListResponseDto> getList(PaymentScheduleInvoiceFilter filter) {
        Page<ActOfTransmissionInvoice> invoiceMappers = repository.findAll(repository.getSpecification(filter), filter.getOrderedPageable());
        return invoiceMappers.map(actOfTransmissionInvoiceMapper::toListDto);
    }

    @Override
    public String getTemplateById(Long id) {
        return thymeleafService.generateActOfTransmissionInvoice(findById(id));
    }

    @Override
    public String getTemplateByActId(Long id) {
        ActOfTransmissionInvoice invoice = repository.findByActOfTransmission_id(id);
        return thymeleafService.generateActOfTransmissionInvoice(invoice);
    }

    @Override
    public ActOfTransmissionInvoiceResponseDto getById(Long id) {
        return actOfTransmissionInvoiceMapper.toDto(findById(id));
    }

    @Override
    public List<ActOfTransmissionInvoice> findInvoicesToSendXfile() {
        return repository.findByXfileIdIsNullAndInvoiceDateBetweenOrderByIdAsc(
                LocalDate.of(2023, 11, 1).atStartOfDay(),
                LocalDate.now().atTime(LocalTime.MAX)
        );
    }

    @Override
    @Transactional
    public void createInvoiceForCron(ActOfTransmission act) {

        ActOfTransmissionInvoice invoice = new ActOfTransmissionInvoice();
        invoice.setStatus(ContractStatus.ACCEPTED);
        ContractForLeasing contract = act.getContract();
        invoice.setContractId(contract.getId());
        invoice.setTotalProductCount(act.getTotalProductCount());
        ModelPriceHistory history = contract.getModelPriceHistory();
        invoice.setServiceName(history.getDeviceModel().getName());

        VatInvoiceCalculationDto calc = CalcUtils.calculateInvoice(history.getPrice(), act.getTotalProductCount(), vatService.getActiveVat().getPercent());
        invoice.setTotalSellPrice(calc.getTotalPriceWithVat());
        invoice.setTotalPrice(calc.getTotalPrice());
        invoice.setTotalVatPrice(calc.getTotalVat());
        invoice.setPriceFor1pc(calc.getPrice());

        Set<ActOfTransmissionDetail> details = act.getDetails();
        for (ActOfTransmissionDetail detail : details) {
            ActOfTransmissionInvoiceDetail invoiceDetail = new ActOfTransmissionInvoiceDetail();
            invoiceDetail.setDeviceId(detail.getDeviceId());

            VatInvoiceCalculationDto calcDetail = CalcUtils.calculateInvoice(detail.getPriceWithVatFor1pc(), 1, vatService.getActiveVat().getPercent());
            invoiceDetail.setPriceWithVatFor1pc(calcDetail.getTotalPriceWithVat());
            invoiceDetail.setPriceFor1pc(calcDetail.getPrice());
            invoiceDetail.setVatFor1pc(calcDetail.getVat());
            invoiceDetail.setVatRate(calcDetail.getVatRate());
            invoice.addDetail(invoiceDetail);
        }
        save(invoice);
        act.setInvoice(invoice);
        actOfTransmissionService.save(act);
    }

    @Override
    public void resendInvoiceToXfileWithNewDate(ResendToXfileRequest dto) {
        ActOfTransmissionInvoice invoice = findById(dto.getId());
        invoice.setInvoiceDate(dto.getDate().atStartOfDay());
        save(invoice);
        xfileProducer.sendActOfTransmissionInvoice(InvoiceToFacturaMapping.mapInvoiceToFactura(invoice));
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
            InvoiceCreateExcelUtil.createBody(invoiceListDtos, sheet, InvoiceType.LEASING_INVOICE);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            byte[] bytes = bos.toByteArray();
            return fileUploadService.upload(bytes,
                            "invoices.xls",
                            "application/vnd.ms-excel",
                            (long) bos.size(), 0L,
                            EntityTypeEnum.LEASING_INVOICE.name(),
                            DocumentType.LEASING_INVOICE.name())
                    .getFileDownloadUri();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public ActOfTransmissionInvoiceResponseDto checkInvoiceXfileStatus(Long id) {
        ActOfTransmissionInvoice invoice = findById(id);
        if (StringUtils.isNotEmpty(invoice.getXfileId())) {
            FacturaCheckStatusDataDto dto = xfileService.checkFacturaStatus(invoice.getXfileId());
            if (!dto.getStatus().equals(invoice.getXfileStatus())) {
                invoice.setXfileStatus(dto.getStatus());
                save(invoice);
            }
        }
        return actOfTransmissionInvoiceMapper.toDto(invoice);
    }

    @Override
    public void updateInvoiceXfileStatus() {
        for (ActOfTransmissionInvoice invoice : repository.updateInvoiceXfileStatus()) {
            xfileProducer.updateActOfTransmissionInvoice(invoice.getId());
        }
    }

    private List<ServiceOrderInvoiceListDto> getInvoices(LocalDate from, LocalDate to, ContractStatus status) {

        return repository.getInvoices(from, to, status);
    }
}
