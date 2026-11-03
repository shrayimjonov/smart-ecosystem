package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.report.StatisticsByBranchReportDTO;
import uz.uzkassa.cts.filter.BaseReportFilter;
import uz.uzkassa.cts.service.ReportService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ReportDeviceByBranchExportExcelHandler extends AbstractExportExcelHandler<BaseReportFilter> {

    ReportService reportService;

    @Override
    public String getFileName(BaseReportFilter filter) {
        StringBuilder fileName = new StringBuilder();
        fileName.append("DeviceStatisticsByBranch");
        if (filter.getFromDate() != null && filter.getToDate() != null) {
            fileName.append("_From_");
            fileName.append(filter.getFromDate());
            fileName.append("_To_");
            fileName.append(filter.getToDate());
        }
        fileName.append(".xlsx");
        return fileName.toString();
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Филиал", "ТТ", "Клиенты", "Заявления", "Фискализации", "Свободные(ККМ)", "Зарегистрированные(ККМ)", "Переданные(ККМ)", "Кол-во(ККМ)", "Свободные(ФМ)", "Зарегистрированные(ФМ)", "Переданные(ФМ)", "Кол-во(ФМ)");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(BaseReportFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        List<StatisticsByBranchReportDTO> result = reportService.getStatisticsByBranchReport(filter);
        int rowIndex = 0;
        for (StatisticsByBranchReportDTO item : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(item.getBranch().getName());
            row.createCell(cellIndex++).setCellValue(item.getCustomerBranchTotal().toString());
            row.createCell(cellIndex++).setCellValue(item.getCustomerTotal().toString());
            row.createCell(cellIndex++).setCellValue(item.getApplicationTotal().toString());
            row.createCell(cellIndex++).setCellValue(item.getFiscalizationTotal().toString());
            row.createCell(cellIndex++).setCellValue(item.getFreeDeviceTotal().toString());
            row.createCell(cellIndex++).setCellValue(item.getRegisteredDeviceTotal().toString());
            row.createCell(cellIndex++).setCellValue(item.getTransferredDeviceTotal().toString());
            row.createCell(cellIndex++).setCellValue(item.getDeviceTotal().toString());
            row.createCell(cellIndex++).setCellValue(item.getFreeFiscalModuleTotal().toString());
            row.createCell(cellIndex++).setCellValue(item.getRegisteredFiscalModuleTotal().toString());
            row.createCell(cellIndex++).setCellValue(item.getTransferredFiscalModuleTotal().toString());
            row.createCell(cellIndex).setCellValue(item.getFiscalizationTotal().toString());
        }
        return workbook;
    }
}
