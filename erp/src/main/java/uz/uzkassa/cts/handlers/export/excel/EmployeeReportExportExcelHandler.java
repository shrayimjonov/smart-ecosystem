package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.report.StatisticsByEmployeeReportDTO;
import uz.uzkassa.cts.filter.EmployeeReportFilter;
import uz.uzkassa.cts.service.ReportService;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class EmployeeReportExportExcelHandler extends AbstractExportExcelHandler<EmployeeReportFilter> {

    ReportService reportService;

    @Override
    public String getFileName(EmployeeReportFilter filter) {
        return "Employee_Report.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Имя", "Филиал", "Клиенты", "Торговые точки", "ККМ", "ФМ", "Тариф", "Тариф (Бонус)", "Сервис", "Сервис (Бонус)", "Лизинг", "Лизинг (Бонус)", "Trade In", "Trade In (Бонус)", "Аренда", "Аренда (Бонус)", "Бонус");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(EmployeeReportFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<StatisticsByEmployeeReportDTO> result = reportService.getStatisticsByEmployeeReport(filter);
        int rowIndex = 0;
        for (StatisticsByEmployeeReportDTO reportDTO : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(reportDTO.getUser()).map(CommonDTO::getName).orElse(""));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(reportDTO.getBranch()).map(CommonDTO::getName).orElse(""));
            row.createCell(cellIndex++).setCellValue(reportDTO.getCustomerTotal());
            row.createCell(cellIndex++).setCellValue(reportDTO.getCustomerBranchTotal());
            row.createCell(cellIndex++).setCellValue(reportDTO.getDeviceTotal());
            row.createCell(cellIndex++).setCellValue(reportDTO.getFiscalModuleTotal());
            row.createCell(cellIndex++).setCellValue(reportDTO.getTariffTotal());
            row.createCell(cellIndex++).setCellValue(reportDTO.getTariffTotalBonus());
            row.createCell(cellIndex++).setCellValue(reportDTO.getServiceTotal());
            row.createCell(cellIndex++).setCellValue(reportDTO.getServiceTotalBonus());
            row.createCell(cellIndex++).setCellValue(reportDTO.getLeasingTotal());
            row.createCell(cellIndex++).setCellValue(reportDTO.getLeasingTotalBonus());
            row.createCell(cellIndex++).setCellValue(reportDTO.getTradeInTotal());
            row.createCell(cellIndex++).setCellValue(reportDTO.getTradeInTotalBonus());
            row.createCell(cellIndex++).setCellValue(reportDTO.getRentTotal());
            row.createCell(cellIndex++).setCellValue(reportDTO.getRentTotalBonus());
            row.createCell(cellIndex).setCellValue(reportDTO.getTariffTotalBonus() + reportDTO.getServiceTotalBonus() + reportDTO.getLeasingTotalBonus() + reportDTO.getTradeInTotalBonus() + reportDTO.getRentTotalBonus());
        }
        return workbook;
    }
}
