package uz.uzkassa.silen.export;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.stereotype.Component;
import uz.uzkassa.silen.domain.ExportReport;
import uz.uzkassa.silen.dto.DailyOperationsFilter;
import uz.uzkassa.silen.dto.DailyOperationsMaterialStats;
import uz.uzkassa.silen.enumeration.ExportReportType;
import uz.uzkassa.silen.service.DailyOperationsService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Slf4j
@Component
@RequiredArgsConstructor
public class DailyOperationsMaterialExcel extends BaseExcelExport implements IExportHandler {
    protected String sheetName = "Материальный отчет";
    private final DailyOperationsService dailyOperationsService;

    @Override
    public ExportReportType getReportType() {
        return ExportReportType.EXCEL_MATERTIAL;
    }

    @Override
    public String getSheetName() {
        return sheetName;
    }

    @Override
    protected void writeHeader(ExportReport exportReport, XSSFSheet sheet) {
        final DailyOperationsFilter filter = readFilter(exportReport.getFilters(), DailyOperationsFilter.class);
        int cellIndex = 0;
        XSSFRow row = sheet.createRow(0);
        row.createCell(cellIndex++).setCellValue("№");
        row.createCell(cellIndex++).setCellValue("Название продукции");
        row.createCell(cellIndex++).setCellValue("Штрих-код");
        row.createCell(cellIndex++).setCellValue(String.format("Остаток %s шт", filter.getFrom().toLocalDate().toString()));
        row.createCell(cellIndex++).setCellValue(String.format("Остаток %s сумма", filter.getFrom().toLocalDate().toString()));
        row.createCell(cellIndex++).setCellValue("Произведено кол-во");
        row.createCell(cellIndex++).setCellValue("Отгружено кол-во");
        row.createCell(cellIndex++).setCellValue("Возвращено кол-во");
        row.createCell(cellIndex++).setCellValue("Списано кол-во");
        row.createCell(cellIndex++).setCellValue(String.format("Остаток %s шт", filter.getTo().toLocalDate().toString()));
        row.createCell(cellIndex).setCellValue(String.format("Остаток %s сумма", filter.getTo().toLocalDate().toString()));
    }

    @Override
    protected void writeBody(ExportReport exportReport, XSSFSheet sheet) {
        final DailyOperationsFilter filter = readFilter(exportReport.getFilters(), DailyOperationsFilter.class);
        int rowIndex = 1;
        final List<DailyOperationsMaterialStats> allIMaterialStatsItems = dailyOperationsService.materialReport(filter);
        for (DailyOperationsMaterialStats materialStats : allIMaterialStatsItems) {
            int cellIndex = 0;
            XSSFRow row = sheet.createRow(rowIndex++);
            final BigDecimal price = Optional.of(materialStats.getPrice()).orElse(BigDecimal.ZERO);
            row.createCell(cellIndex++).setCellValue(materialStats.getProductName());
            row.createCell(cellIndex++).setCellValue(materialStats.getBarcode());
            row.createCell(cellIndex++).setCellValue(price.toString());
            row.createCell(cellIndex++).setCellValue(materialStats.getStartAmount().toString());
            row.createCell(cellIndex++).setCellValue(price.multiply(materialStats.getStartAmount()).toString());
            row.createCell(cellIndex++).setCellValue(materialStats.getProductionQty().toString());
            row.createCell(cellIndex++).setCellValue(materialStats.getShippedQty().toString());
            row.createCell(cellIndex++).setCellValue(materialStats.getReturnedQty().toString());
            row.createCell(cellIndex++).setCellValue(materialStats.getDropoutQty().toString());
            row.createCell(cellIndex++).setCellValue(materialStats.getEndAmount().toString());
            row.createCell(cellIndex).setCellValue(price.multiply(materialStats.getEndAmount()).toString());
        }
    }

}
