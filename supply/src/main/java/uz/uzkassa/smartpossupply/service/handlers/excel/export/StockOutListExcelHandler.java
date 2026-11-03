package uz.uzkassa.smartpossupply.service.handlers.excel.export;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.stockout.StockOutListDTO;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.service.StockOutService;

import java.util.LinkedList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StockOutListExcelHandler extends AbstractExcelHandler<StockFilter> {

    StockOutService stockOutService;

    @Override
    public String getFileName(StockFilter filter) {
        StringBuilder fileName = new StringBuilder();
        fileName.append("Stock_Out_List");
        if (filter.getFrom() != null && filter.getTo() != null) {
            fileName.append("_From_");
            fileName.append(filter.getFrom().toLocalDate().toString());
            fileName.append("_To_");
            fileName.append(filter.getTo().toLocalDate().toString());
        }
        fileName.append(".xlsx");
        return fileName.toString();
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("Номер");
        header.add("Дата отгрузки");
        header.add("Клиент");
        header.add("Филиал");
        header.add("Склад");
        header.add("Заказ");
        header.add("Создана от");
        header.add("Сумма");
        header.add("Статус");
        return header;
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(StockFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        List<StockOutListDTO> stocks = stockOutService.getList(filter).getContent();
        int rowIndex = 0;
        for (StockOutListDTO stock : stocks) {
            Row row = sheet.createRow(++rowIndex);
            row.createCell(0).setCellValue(stock.getStockNumber());
            row.createCell(1).setCellValue(stock.getTransferDate().toLocalDate().toString());
            row.createCell(2).setCellValue(stock.getCustomer().getName());
            row.createCell(3).setCellValue(stock.getBranch().getName());
            row.createCell(4).setCellValue(stock.getWarehouse() != null ? stock.getWarehouse().getName() : "");
            row.createCell(5).setCellValue(stock.getOrder() != null ? stock.getOrder().getName() + " " : "");
            row.createCell(6).setCellValue(stock.getOldStock() != null ? stock.getOldStock().getName() : "");
            row.createCell(7).setCellValue(stock.getTotalPrice().toString());
            row.createCell(8).setCellValue(stock.getStatus().getName());
        }
        return workbook;
    }
}
