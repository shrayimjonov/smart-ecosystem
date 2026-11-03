package uz.uzkassa.smartpossupply.service.handlers.excel.export;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.stockadjustment.StockAdjustmentListDTO;
import uz.uzkassa.smartpossupply.filters.StockAdjustmentFilter;
import uz.uzkassa.smartpossupply.service.StockAdjustmentService;

import java.util.LinkedList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StockAdjustmentListExcelHandler extends AbstractExcelHandler<StockAdjustmentFilter> {

    StockAdjustmentService stockAdjustmentService;

    @Override
    public String getFileName(StockAdjustmentFilter filter) {
        return "Stock_Adjustments.xlsx";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("Номер");
        header.add("Дата корректировки");
        header.add("Филиал");
        header.add("Склад");
        header.add("Количество");
        header.add("Статус");
        return header;
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(StockAdjustmentFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        List<StockAdjustmentListDTO> stockAdjustments = stockAdjustmentService.getList(filter).getContent();
        int rowIndex = 0;
        for (StockAdjustmentListDTO stockAdjustment : stockAdjustments) {
            Row row = sheet.createRow(++rowIndex);
            row.createCell(0).setCellValue(stockAdjustment.getAdjustmentNumber());
            row.createCell(1).setCellValue(stockAdjustment.getAdjustmentDate().toLocalDate().toString());
            row.createCell(2).setCellValue(stockAdjustment.getBranch().getName());
            row.createCell(3).setCellValue(stockAdjustment.getWarehouse().getName());
            row.createCell(4).setCellValue(stockAdjustment.getTotalQty().toString());
            row.createCell(5).setCellValue(stockAdjustment.getStatus().getName());
        }
        return workbook;
    }
}
