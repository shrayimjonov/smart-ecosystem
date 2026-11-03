package uz.uzkassa.smartpossupply.service.handlers.excel.export;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.materialreport.MaterialReportDTO;
import uz.uzkassa.smartpossupply.dto.materialreport.MaterialReportItemDTO;
import uz.uzkassa.smartpossupply.filters.MaterialReportFilter;
import uz.uzkassa.smartpossupply.service.MaterialReportService;

import java.util.LinkedList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class MaterialReportExcelHandler extends AbstractExcelHandler<MaterialReportFilter> {

    MaterialReportService materialReportService;

    @Override
    public String getFileName(MaterialReportFilter filter) {
        return "MaterialReport.xlsx";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("Товар");
        header.add("Склад");
        header.add("Начальный остаток, шт.");
        header.add("Начальный остаток, сумма");
        header.add("Приход, кол-во");
        header.add("Приход, сумма");
        header.add("Отгружено, кол-во");
        header.add("Отгружено, сумма");
        header.add("Остаток, шт.");
        header.add("Остаток, сумма");
        return header;
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(MaterialReportFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        List<MaterialReportDTO> report = materialReportService.getReport(filter);
        int rowIndex = 0;
        for (MaterialReportDTO materialReportDTO : report) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(materialReportDTO.getBranchName());
            createHeader(getHeaders(), sheet, rowIndex++);
            for (MaterialReportItemDTO item : materialReportDTO.getItems()) {
                row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(item.getProductName().concat(": ").concat(item.getProductBarcode()));
                row.createCell(1).setCellValue(item.getWarehouseName());
                row.createCell(2).setCellValue(item.getInitialQty().toString());
                row.createCell(3).setCellValue(item.getInitialPrice().toString());
                row.createCell(4).setCellValue(item.getStockInQty().toString());
                row.createCell(5).setCellValue(item.getStockInPrice().toString());
                row.createCell(6).setCellValue(item.getStockOutQty().toString());
                row.createCell(7).setCellValue(item.getStockOutPrice().toString());
                row.createCell(8).setCellValue(item.getLastQty().toString());
                row.createCell(9).setCellValue(item.getLastPrice().toString());
            }
            row = sheet.createRow(rowIndex++);
            row.createCell(1).setCellValue("Итого");
            row.createCell(2).setCellValue(materialReportDTO.getTotalInitialQty().toString());
            row.createCell(3).setCellValue(materialReportDTO.getTotalInitialPrice().toString());
            row.createCell(4).setCellValue(materialReportDTO.getTotalStockInQty().toString());
            row.createCell(5).setCellValue(materialReportDTO.getTotalStockInPrice().toString());
            row.createCell(6).setCellValue(materialReportDTO.getTotalStockOutQty().toString());
            row.createCell(7).setCellValue(materialReportDTO.getTotalStockOutPrice().toString());
            row.createCell(8).setCellValue(materialReportDTO.getTotalLastQty().toString());
            row.createCell(9).setCellValue(materialReportDTO.getTotalLastPrice().toString());
        }
        return workbook;
    }
}
