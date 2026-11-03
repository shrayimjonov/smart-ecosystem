package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.stock.Stock;
import uz.uzkassa.cts.filter.StockFilter;
import uz.uzkassa.cts.repository.StockRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StockOutExportExcelHandler extends AbstractExportExcelHandler<StockFilter> {

    StockRepository stockRepository;

    @Override
    public String getFileName(StockFilter filter) {
        return "Stock_Outs.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Номер", "Компания", "Филиал", "Склад", "Отдел", "Дата", "Статус");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(StockFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<Stock> result = stockRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (Stock stock : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(stock.getStockNumber());
            row.createCell(cellIndex++).setCellValue(stock.getCompany().getName());
            row.createCell(cellIndex++).setCellValue(stock.getBranch().getName());
            row.createCell(cellIndex++).setCellValue(stock.getWarehouse().getName());
            row.createCell(cellIndex++).setCellValue(stock.getDepartment().getName());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(stock.getStockDate()));
            row.createCell(cellIndex).setCellValue(stock.getStatus().getName());
        }
        return workbook;
    }
}
