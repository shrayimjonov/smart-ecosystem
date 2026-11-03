package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.stock.StockAdjustment;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.StockFilter;
import uz.uzkassa.cts.repository.StockAdjustmentRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.LinkedList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StockAdjustmentExportExcelHandler extends AbstractExportExcelHandler<StockFilter> {

    StockAdjustmentRepository stockAdjustmentRepository;

    @Override
    public String getFileName(StockFilter filter) {
        return "Stock_Adjustments.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Номер", "Компания", "Филиал", "Склад", "Дата", "Статус");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(StockFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<StockAdjustment> result = stockAdjustmentRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (StockAdjustment stockAdjustment : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(stockAdjustment.getAdjustmentNumber());
            row.createCell(cellIndex++).setCellValue(stockAdjustment.getCompany().getName());
            row.createCell(cellIndex++).setCellValue(stockAdjustment.getBranch().getName());
            row.createCell(cellIndex++).setCellValue(stockAdjustment.getWarehouse().getName());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(stockAdjustment.getAdjustmentDate()));
            row.createCell(cellIndex).setCellValue(stockAdjustment.getStatus().getName());
        }
        return workbook;
    }
}
