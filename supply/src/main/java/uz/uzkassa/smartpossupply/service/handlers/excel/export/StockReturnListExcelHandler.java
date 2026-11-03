package uz.uzkassa.smartpossupply.service.handlers.excel.export;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.stockreturn.StockReturnListDTO;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.service.StockReturnService;

import java.util.LinkedList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StockReturnListExcelHandler extends AbstractExcelHandler<StockFilter> {

    StockReturnService stockReturnService;

    @Override
    public String getFileName(StockFilter filter) {
        StringBuilder fileName = new StringBuilder();
        fileName.append("Stock_Returns");
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
        header.add("Дата возврата");
        header.add("Клиент");
        header.add("Филиал");
        header.add("Склад");
        header.add("Статус");
        return header;
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(StockFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        List<StockReturnListDTO> stocks = stockReturnService.getList(filter).getContent();
        int rowIndex = 0;
        for (StockReturnListDTO stockReturnListDTO : stocks) {
            Row row = sheet.createRow(++rowIndex);
            row.createCell(0).setCellValue(stockReturnListDTO.getStockNumber());
            row.createCell(1).setCellValue(stockReturnListDTO.getTransferDate().toLocalDate().toString());
            row.createCell(2).setCellValue(stockReturnListDTO.getCustomer() != null ? stockReturnListDTO.getCustomer().getName() : "");
            row.createCell(3).setCellValue(stockReturnListDTO.getBranch().getName());
            row.createCell(4).setCellValue(stockReturnListDTO.getWarehouse().getName());
            row.createCell(5).setCellValue(stockReturnListDTO.getStatus().getName());
        }
        return workbook;
    }
}
