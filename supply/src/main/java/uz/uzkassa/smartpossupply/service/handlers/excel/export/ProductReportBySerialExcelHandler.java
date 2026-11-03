package uz.uzkassa.smartpossupply.service.handlers.excel.export;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.report.ProductSerialReportDTO;
import uz.uzkassa.smartpossupply.filters.BalanceReportFilter;
import uz.uzkassa.smartpossupply.service.ProductSerialService;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductReportBySerialExcelHandler extends AbstractExcelHandler<BalanceReportFilter> {

    ProductSerialService productSerialService;

    @Override
    public String getFileName(BalanceReportFilter filter) {
        return "ProductReportBySerial.xlsx";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("Название товара");
        header.add("Штрихкод товара");
        header.add("ИКПУ");
        header.add("Серия");
        header.add("Филиал");
        header.add("Склад");
        header.add("Кол-во");
        header.add("Цена");
        header.add("Сумма");
        return header;
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(BalanceReportFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        List<ProductSerialReportDTO> productReport = productSerialService.getReport(filter).getContent();
        int rowIndex = 0;
        for (ProductSerialReportDTO productReportListDTO : productReport) {
            Row row = sheet.createRow(++rowIndex);
            row.createCell(0).setCellValue(productReportListDTO.getProduct().getName());
            row.createCell(1).setCellValue(productReportListDTO.getProduct().getBarcode());
            row.createCell(2).setCellValue(productReportListDTO.getProduct().getVatBarcode());
            row.createCell(3).setCellValue(productReportListDTO.getProductSerial().getName());
            row.createCell(4).setCellValue(productReportListDTO.getBranch().getName());
            row.createCell(5).setCellValue(productReportListDTO.getWarehouse().getName());
            row.createCell(6).setCellValue(productReportListDTO.getQty().toString());
            row.createCell(7).setCellValue(Optional.ofNullable(productReportListDTO.getPrice()).orElse(BigDecimal.ZERO).toString());
            row.createCell(8).setCellValue(productReportListDTO.getTotalPrice().toString());
        }
        return workbook;
    }
}
