package uz.uzkassa.smartpossupply.service.handlers.excel.export;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.stockin.StockInListDTO;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.service.StockInService;

import java.util.LinkedList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StockInListExcelHandler extends AbstractExcelHandler<StockFilter> {

    StockInService stockInService;

    @Override
    public String getFileName(StockFilter filter) {
        StringBuilder fileName = new StringBuilder();
        fileName.append("Stock_In_List");
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
        header.add("Дата прихода");
        header.add("Поставщик");
        header.add("Филиал");
        header.add("Склад");
        header.add("Создан от");
        header.add("Сумма прихода");
        header.add("Статус");
        return header;
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(StockFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        List<StockInListDTO> stocks = stockInService.getList(filter).getContent();
        int rowIndex = 0;
        for (StockInListDTO stockInListDTO : stocks) {
            Row row = sheet.createRow(++rowIndex);
            row.createCell(0).setCellValue(stockInListDTO.getStockNumber());
            row.createCell(1).setCellValue(stockInListDTO.getTransferDate().toLocalDate().toString());
            row.createCell(2).setCellValue(stockInListDTO.getSupplier() != null ? stockInListDTO.getSupplier().getName() : "");
            row.createCell(3).setCellValue(stockInListDTO.getBranch().getName());
            row.createCell(4).setCellValue(stockInListDTO.getWarehouse().getName());
            row.createCell(5).setCellValue(stockInListDTO.getOldStock() != null ? stockInListDTO.getOldStock().getName() : "");
            row.createCell(6).setCellValue(stockInListDTO.getTotalPrice().toString());
            row.createCell(7).setCellValue(stockInListDTO.getStatus().getName());
        }
        return workbook;
    }
}
