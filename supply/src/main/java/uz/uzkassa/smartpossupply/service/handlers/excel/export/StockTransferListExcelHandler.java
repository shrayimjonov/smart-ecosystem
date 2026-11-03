package uz.uzkassa.smartpossupply.service.handlers.excel.export;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.stocktransfer.StockTransferListDTO;
import uz.uzkassa.smartpossupply.filters.StockTransferFilter;
import uz.uzkassa.smartpossupply.service.StockTransferService;

import java.util.LinkedList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StockTransferListExcelHandler extends AbstractExcelHandler<StockTransferFilter> {

    StockTransferService stockTransferService;

    @Override
    public String getFileName(StockTransferFilter filter) {
        return "Stock_Transfers.xlsx";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("Номер");
        header.add("Филиал отгрузка ");
        header.add("Склад отгрузка");
        header.add("Филиал приход");
        header.add("Склад приход");
        header.add("Количество");
        header.add("Дата");
        header.add("Статус");
        return header;
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(StockTransferFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        List<StockTransferListDTO> stockTransfers = stockTransferService.getList(filter).getContent();
        int rowIndex = 0;
        for (StockTransferListDTO stockTransfer : stockTransfers) {
            Row row = sheet.createRow(++rowIndex);
            row.createCell(0).setCellValue(stockTransfer.getTransferNumber());
            row.createCell(1).setCellValue(stockTransfer.getFromBranch().getName());
            row.createCell(2).setCellValue(stockTransfer.getFromWarehouse().getName());
            row.createCell(3).setCellValue(stockTransfer.getToBranch().getName());
            row.createCell(4).setCellValue(stockTransfer.getToWarehouse().getName());
            row.createCell(5).setCellValue(stockTransfer.getTotalQty().toString());
            row.createCell(6).setCellValue(stockTransfer.getTransferDate().toLocalDate().toString());
            row.createCell(7).setCellValue(stockTransfer.getStatus().getName());
        }
        return workbook;
    }
}
