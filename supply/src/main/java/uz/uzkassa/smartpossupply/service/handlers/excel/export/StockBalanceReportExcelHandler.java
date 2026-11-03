package uz.uzkassa.smartpossupply.service.handlers.excel.export;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.report.WarehouseProductReportDTO;
import uz.uzkassa.smartpossupply.filters.BalanceReportFilter;
import uz.uzkassa.smartpossupply.service.WarehouseProductService;

import java.util.LinkedList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StockBalanceReportExcelHandler extends AbstractExcelHandler<BalanceReportFilter> {

    WarehouseProductService warehouseProductService;

    @Override
    public String getFileName(BalanceReportFilter filter) {
        return "StockBalanceReport.xlsx";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("Наименование");
        header.add("ИКПУ");
        header.add("Штрих-код");
        header.add("Филиал");
        header.add("Склад");
        header.add("Кол-во");
        return header;
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(BalanceReportFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        List<WarehouseProductReportDTO> stockBalanceReports = warehouseProductService.getReport(filter).stream().toList();
        int rowIndex = 0;
        for (WarehouseProductReportDTO stockBalanceReport : stockBalanceReports) {
            Row row = sheet.createRow(++rowIndex);
            row.createCell(0).setCellValue(stockBalanceReport.getProduct().getName());
            row.createCell(1).setCellValue(stockBalanceReport.getProduct().getVatBarcode());
            row.createCell(2).setCellValue(stockBalanceReport.getProduct().getBarcode());
            row.createCell(3).setCellValue(stockBalanceReport.getBranch().getName());
            row.createCell(4).setCellValue(stockBalanceReport.getWarehouse().getName());
            row.createCell(5).setCellValue(stockBalanceReport.getQty().toPlainString());
        }
        return workbook;
    }
}
