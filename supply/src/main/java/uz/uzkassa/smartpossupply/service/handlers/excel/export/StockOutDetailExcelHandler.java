package uz.uzkassa.smartpossupply.service.handlers.excel.export;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.domain.stock.StockProduct;
import uz.uzkassa.smartpossupply.filters.StockProductFilter;
import uz.uzkassa.smartpossupply.repository.StockProductRepository;
import uz.uzkassa.smartpossupply.web.rest.errors.NotFoundException;

import java.util.LinkedList;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 06.12.2022 18:57
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StockOutDetailExcelHandler extends AbstractExcelHandler<StockProductFilter> {

    StockProductRepository stockProductRepository;

    @Override
    public String getFileName(StockProductFilter filter) {
        return "Stock_Out_Detail.xlsx";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("Наименование");
        header.add("Ед. Изм");
        header.add("Количество");
        header.add("Цена");
        header.add("Сумма без НДС");
        header.add("НДС");
        header.add("Сумма с НДС");
        return header;
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(StockProductFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        StockProduct stockProduct = stockProductRepository.findById(filter.getStockProductId()).orElseThrow(() -> new NotFoundException("Stock product not found by id " + filter.getStockProductId()));
        int rowIndex = 0;
        Row row = sheet.createRow(++rowIndex);
        row.createCell(0).setCellValue(stockProduct.getProduct().getName());
        row.createCell(1).setCellValue(stockProduct.getUnit().getName());
        row.createCell(2).setCellValue(stockProduct.getQty().toString());
        row.createCell(3).setCellValue(stockProduct.getSellingPriceWithoutVat().toString());
        row.createCell(4).setCellValue(stockProduct.getTotalSellingPriceWithoutVat().toString());
        row.createCell(5).setCellValue(stockProduct.getTotalVatPrice().toString());
        row.createCell(6).setCellValue(stockProduct.getTotalSellingPrice().toString());
        return workbook;
    }
}
