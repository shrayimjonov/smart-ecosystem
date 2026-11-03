package uz.uzkassa.silen.export;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import uz.uzkassa.silen.domain.DailyOperations;
import uz.uzkassa.silen.domain.ExportReport;
import uz.uzkassa.silen.dto.DailyOperationsFilter;
import uz.uzkassa.silen.enumeration.ExportReportType;
import uz.uzkassa.silen.repository.pg.DailyOperationsRepository;

import java.math.BigDecimal;
import java.util.Optional;


@Slf4j
@Component
@RequiredArgsConstructor
public class DailyOperationsExcel extends BaseExcelExport implements IExportHandler {
    private final DailyOperationsRepository dailyOperationsRepository;
    protected String sheetName = "Материальный отчет";

    @Override
    public ExportReportType getReportType() {
        return ExportReportType.DAILY_OPERATIONS;
    }

    @Override
    public String getSheetName() {
        return sheetName;
    }

    @Override
    protected void writeHeader(ExportReport exportReport, XSSFSheet sheet) {
        final DailyOperationsFilter filter = readFilter(exportReport.getFilters(), DailyOperationsFilter.class);
        int rowIndex = 0;
        int cellIndex = 0;
        XSSFRow row = sheet.createRow(rowIndex);
        row.createCell(cellIndex++).setCellValue("№");
        row.createCell(cellIndex++).setCellValue("Название продукции");
        row.createCell(cellIndex++).setCellValue("Штрих-код");
        row.createCell(cellIndex++).setCellValue(String.format("Остаток %s шт", filter.getFrom().toLocalDate().toString()));
        row.createCell(cellIndex++).setCellValue(String.format("Остаток %s сумма", filter.getFrom().toLocalDate().toString()));
        row.createCell(cellIndex++).setCellValue("Тип операции");
        row.createCell(cellIndex++).setCellValue("Кол-во по операции");
        row.createCell(cellIndex++).setCellValue("Дата и время");
        row.createCell(cellIndex++).setCellValue(String.format("Остаток %s шт", filter.getTo().toLocalDate().toString()));
        row.createCell(cellIndex).setCellValue(String.format("Остаток %s сумма", filter.getTo().toLocalDate().toString()));
    }

    @Override
    protected void writeBody(ExportReport exportReport, XSSFSheet sheet) {
        final DailyOperationsFilter filter = readFilter(exportReport.getFilters(), DailyOperationsFilter.class);
        boolean hasNext;
        int rowIndex = 1;
        int pageNumber = 0;
        do {
            filter.setPage(pageNumber++);
            final Page<DailyOperations> byFilter = dailyOperationsRepository.findAllByFilter(filter);
            for (DailyOperations dailyOperation : byFilter) {
                int cellIndex = 0;
                XSSFRow row = sheet.createRow(rowIndex++);
                final BigDecimal price = Optional.of(dailyOperation.getProduct().getPrice()).orElse(BigDecimal.ZERO);
                row.createCell(cellIndex++).setCellValue(dailyOperation.getProduct().getVisibleName());
                row.createCell(cellIndex++).setCellValue(dailyOperation.getProduct().getBarcode());
                row.createCell(cellIndex++).setCellValue(dailyOperation.getProduct().getPrice().toString());
                row.createCell(cellIndex++).setCellValue(dailyOperation.getAmountStart().toString());
                row.createCell(cellIndex++).setCellValue(price.multiply(dailyOperation.getAmountStart()).toString());
                row.createCell(cellIndex++).setCellValue(dailyOperation.getType().getNameRu());
                row.createCell(cellIndex++).setCellValue(dailyOperation.getAmount().toString());
                row.createCell(cellIndex++).setCellValue(dailyOperation.getOperationDate().toString());
                row.createCell(cellIndex++).setCellValue(dailyOperation.getAmountEnd().toString());
                row.createCell(cellIndex).setCellValue(price.multiply(dailyOperation.getAmountEnd()).toString());
            }
            hasNext = byFilter.hasNext();
        } while (hasNext);
    }

}
