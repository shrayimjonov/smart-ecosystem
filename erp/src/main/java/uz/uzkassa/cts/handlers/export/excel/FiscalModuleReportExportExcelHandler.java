package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.FiscalModuleReport;
import uz.uzkassa.cts.domain.address.BaseAddress;
import uz.uzkassa.cts.domain.address.Region;
import uz.uzkassa.cts.enums.ArrivalStatus;
import uz.uzkassa.cts.enums.FMReportReason;
import uz.uzkassa.cts.filter.FiscalModuleReportFilter;
import uz.uzkassa.cts.repository.FiscalModuleReportRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FiscalModuleReportExportExcelHandler extends AbstractExportExcelHandler<FiscalModuleReportFilter> {

    FiscalModuleReportRepository fiscalModuleReportRepository;

    @Override
    public String getFileName(FiscalModuleReportFilter filter) {
        return "Fiscal_Module_Report.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("ИНН", "Клиент", "ККМ", "ФМ", "Статус принятие кейса", "Статус прихода ФМ", "Дата прихода ФМ", "Оператор", "Тип кейса", "Регион", "Кейс ID", "Дата принятие кейса");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(FiscalModuleReportFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<FiscalModuleReport> result = fiscalModuleReportRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (FiscalModuleReport fiscalModuleReport : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(fiscalModuleReport.getCustomer().getTin());
            row.createCell(cellIndex++).setCellValue(fiscalModuleReport.getCustomer().getName());
            row.createCell(cellIndex++).setCellValue(fiscalModuleReport.getDeviceSerialNumber());
            row.createCell(cellIndex++).setCellValue(fiscalModuleReport.getFmSerialNumber());
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(fiscalModuleReport.getReason()).map(FMReportReason::getName).orElse(""));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(fiscalModuleReport.getArrivalStatus()).map(ArrivalStatus::getName).orElse(""));
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(fiscalModuleReport.getArrivalDate()));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(fiscalModuleReport.getCases()).map(cases -> cases.getCreator().getName()).orElse(""));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(fiscalModuleReport.getCases()).map(cases -> cases.getCaseType().getName()).orElse(""));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(fiscalModuleReport.getCustomer().getAddress()).map(BaseAddress::getRegion).map(Region::getName).orElse(""));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(fiscalModuleReport.getCaseId()).map(Object::toString).orElse(""));
            row.createCell(cellIndex).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(fiscalModuleReport.getCreatedDate()));
        }
        return workbook;
    }
}
