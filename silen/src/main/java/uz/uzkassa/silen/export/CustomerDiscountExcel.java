package uz.uzkassa.silen.export;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import uz.uzkassa.silen.domain.CustomerProduct;
import uz.uzkassa.silen.domain.ExportReport;
import uz.uzkassa.silen.dto.filter.CustomerFilter;
import uz.uzkassa.silen.enumeration.ExportReportType;
import uz.uzkassa.silen.repository.pg.CustomerProductRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerDiscountExcel extends BaseExcelExport implements IExportHandler {

    private final CustomerProductRepository customerProductRepository;
    private final List<String> headers = Arrays.asList(
        "НАИМЕНОВАНИЕ ТОВАРА",
        "СКИДКА %", "СКИДКА UZS", "СТОИМОСТЬ"
    );
    int[] columnWidth = new int[]{170, 130, 130, 130, 100};

    @Override
    public ExportReportType getReportType() {
        return ExportReportType.EXCEL_CUSTOMER_DISCOUNT;
    }

    @Override
    protected void writeHeader(ExportReport exportReport, XSSFSheet sheet) {
        sheetName = "Discount Products";
        Row row = sheet.createRow(0);
        CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        // create font
        XSSFFont font = sheet.getWorkbook().createFont();
        font.setBold(true);
        font.setFontHeight(10);
        headerStyle.setFont(font);
        // style alignment
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setWrapText(true);

        // write header row
        for (int i = 0; i < headers.size(); i++) {
            sheet.setColumnWidth(i, 55 * columnWidth[i]);
            sheet.autoSizeColumn(i);
            createCell(row, i, headers.get(i), headerStyle);
        }
    }

    @Override
    protected void writeBody(ExportReport exportReport, XSSFSheet sheet) {
        final CustomerFilter filter = readFilter(exportReport.getFilters(), CustomerFilter.class);
        boolean hasNext;
        int pageNumber = 0;
        int rowNum = 1;
        do {
            filter.setPage(pageNumber++);
            Page<CustomerProduct> allByFilter = customerProductRepository.findAllByFilter(filter);

            CellStyle style = sheet.getWorkbook().createCellStyle();
            DataFormat fmt = sheet.getWorkbook().createDataFormat();
            style.setDataFormat(fmt.getFormat("@"));
            for (CustomerProduct product : allByFilter) {
                Row row = sheet.createRow(rowNum);
                getRow(row, product, style);
                rowNum++;
            }
            hasNext = allByFilter.hasNext();
        } while (hasNext);
    }

    private void createCell(Row row, int columnIndex, Object value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void getRow(Row row, CustomerProduct product, CellStyle style) {
        int colNum = 0;
        row.setRowStyle(style);
        Cell cell2 = row.createCell(colNum++);
        cell2.setCellValue(product.getProduct().getName());

        Cell cell3 = row.createCell(colNum++);
        cell3.setCellValue(product.getDiscountPercent() != null ? product.getDiscountPercent().toString() : BigDecimal.ZERO.toString());

        Cell cell4 = row.createCell(colNum++);
        cell4.setCellValue(product.getDiscount() != null ? product.getDiscount().toString() : BigDecimal.ZERO.toString());

        Cell cell5 = row.createCell(colNum);
        cell5.setCellValue(Optional.ofNullable(product.getProduct().getPrice()).orElse(BigDecimal.ZERO) + "");
    }
}
