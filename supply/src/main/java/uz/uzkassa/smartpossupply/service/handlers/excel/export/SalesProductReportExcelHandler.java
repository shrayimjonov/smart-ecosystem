package uz.uzkassa.smartpossupply.service.handlers.excel.export;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.stockreport.StockReportByProductDTO;
import uz.uzkassa.smartpossupply.enums.TransferType;
import uz.uzkassa.smartpossupply.filters.StockReportByProductFilter;
import uz.uzkassa.smartpossupply.service.StockReportService;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SalesProductReportExcelHandler extends AbstractExcelHandler<StockReportByProductFilter> {
    StockReportService stockReportService;

    @Override
    public String getFileName(StockReportByProductFilter filter) {
        return "SalesProductReport.xlsx";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("№");
        header.add("Товар");
        header.add("Штрих код");
        header.add("Кол-во шт");
        header.add("Кол-во бл");
        header.add("Сумма");
        header.add("Кол-во клиентов");
        return header;
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(StockReportByProductFilter filter) {
        filter.setSize(1000); //max rows
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        List<StockReportByProductDTO> productReport = stockReportService.getStockReport(filter).getContent();
        int rowIndex = 0;
        for (StockReportByProductDTO dto : productReport) {
            Row row = sheet.createRow(++rowIndex);
            row.createCell(0).setCellValue(rowIndex);
            row.createCell(1).setCellValue(Optional.ofNullable(dto.getProductName()).orElse(""));
            row.createCell(2).setCellValue(Optional.ofNullable(dto.getBarcode()).orElse(""));
            row.createCell(3).setCellValue(Optional.ofNullable(dto.getTotalQty()).orElse(BigDecimal.ZERO).toString());
            row.createCell(4).setCellValue(Optional.ofNullable(dto.getTotalQtyInBox()).orElse(BigDecimal.ZERO).toString());
            if (TransferType.OUT.name().equals(filter.getTransferType())) {
                row.createCell(5).setCellValue(Optional.ofNullable(dto.getTotalSellingPrice()).orElse(BigDecimal.ZERO).toString());
            } else if (TransferType.IN.name().equals(filter.getTransferType())) {
                row.createCell(5).setCellValue(Optional.ofNullable(dto.getTotalPurchasePrice()).orElse(BigDecimal.ZERO).toString());
            }
            row.createCell(6).setCellValue(Optional.ofNullable(dto.getTotalCustomerCount()).orElse(0L).toString());
        }
        return workbook;
    }
}
