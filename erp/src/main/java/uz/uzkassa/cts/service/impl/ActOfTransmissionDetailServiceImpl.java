package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.domain.ActOfTransmissionDetail;
import uz.uzkassa.cts.dto.actoftransmissiondetail.response.ActOfTransmissionDetailListResponseDto;
import uz.uzkassa.cts.enums.DocumentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.ActOfTransmissionDetailFilter;
import uz.uzkassa.cts.mappings.ActOfTransmissionDetailMapper;
import uz.uzkassa.cts.repository.ActOfTransmissionDetailRepository;
import uz.uzkassa.cts.repository.ServicePaymentHistoryForCronRepository;
import uz.uzkassa.cts.service.ActOfTransmissionDetailService;
import uz.uzkassa.cts.service.ActPaymentScheduleService;
import uz.uzkassa.cts.service.FileUploadService;
import uz.uzkassa.cts.service.LedgerService;
import uz.uzkassa.cts.utils.DateUtil;
import uz.uzkassa.cts.utils.InvoiceCreateExcelUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class ActOfTransmissionDetailServiceImpl extends BaseServiceImpl<ActOfTransmissionDetail, Long> implements ActOfTransmissionDetailService {

    private final ActOfTransmissionDetailRepository repository;
    private final ActPaymentScheduleService actPaymentScheduleService;
    private final FileUploadService fileUploadService;
    private final LedgerService ledgerService;
    private final ServicePaymentHistoryForCronRepository servicePaymentHistoryForCronRepository;
    private final ActOfTransmissionDetailMapper actOfTransmissionDetailMapper;

    public ActOfTransmissionDetailServiceImpl(ActOfTransmissionDetailRepository repository,
                                              ActPaymentScheduleService actPaymentScheduleService,
                                              FileUploadService fileUploadService,
                                              LedgerService ledgerService,
                                              ServicePaymentHistoryForCronRepository servicePaymentHistoryForCronRepository,
                                              ActOfTransmissionDetailMapper actOfTransmissionDetailMapper) {
        super(repository);
        this.repository = repository;
        this.actPaymentScheduleService = actPaymentScheduleService;
        this.fileUploadService = fileUploadService;
        this.ledgerService = ledgerService;
        this.servicePaymentHistoryForCronRepository = servicePaymentHistoryForCronRepository;
        this.actOfTransmissionDetailMapper = actOfTransmissionDetailMapper;
    }


    @Override
    public Page<ActOfTransmissionDetailListResponseDto> getList(ActOfTransmissionDetailFilter filter) {
        Specification<ActOfTransmissionDetail> specification = getSpecification(filter);
        Page<ActOfTransmissionDetail> details = repository.findAll(specification, filter.getOrderedPageable());
        return details.map(actOfTransmissionDetailMapper::toListDto);
    }

    private Specification<ActOfTransmissionDetail> getSpecification(ActOfTransmissionDetailFilter filter) {
        return repository.getSpecification(filter);
    }

    @Override
    public BigDecimal getCurrentMonthIncomeExpectation() {
        LocalDate from = DateUtil.getMonthFirstDate();
        LocalDate to = DateUtil.getMonthLastDate();
        return actPaymentScheduleService.getCurrentMonthExpectation(from, to);
    }

    @Override
    public BigDecimal getTotalIncome(ActOfTransmissionDetailFilter filter) {
        return repository.getTotalIncome(filter);
    }

    @Override
    public String getDebtors() {
        List<Object[]> result = repository.resultDebtors();
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Debtors");
            String[] headerNames = new String[]{"Количество ККМ", "Номер договора", "Дата создания договора",
                "Номер акта", "Дата создания акта", "Название компании",
                "ИНН", "Телефон", "Название региона", "Имя создателя",
                "Филиал ЦТО", "Остаток", "Предоплата",
                "Дата принятия", "Статус документов", "Причина"};
            InvoiceCreateExcelUtil.createHeader(headerNames, sheet);
            InvoiceCreateExcelUtil.createActBody(result, sheet, servicePaymentHistoryForCronRepository, ledgerService);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            byte[] bytes = bos.toByteArray();
            return fileUploadService.upload(bytes,
                    "actDebtors.xls",
                    "application/vnd.ms-excel",
                    (long) bos.size(), 0L,
                    EntityTypeEnum.ACT_OF_TRANSMISSION.name(),
                    DocumentType.ACT_OF_TRANSMISSION.name())
                .getFileDownloadUri();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public byte[] download(ActOfTransmissionDetailFilter filter) {
        Specification<ActOfTransmissionDetail> specification = getSpecification(filter);
        List<ActOfTransmissionDetail> details = repository.findAll(specification);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("ActDetails");
            String[] headerNames = new String[]{"Контракт", "Акт", "ИНН",
                "Клиент", "ККМ", "Сумма", "Период", "Статус акта", "Статус физ. документа", "Причина"};
            InvoiceCreateExcelUtil.createHeader(headerNames, sheet);
            InvoiceCreateExcelUtil.createActOfTransmissionDetails(details, sheet);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return new byte[0];
    }
}
