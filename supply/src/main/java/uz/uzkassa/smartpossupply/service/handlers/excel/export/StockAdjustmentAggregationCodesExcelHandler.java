package uz.uzkassa.smartpossupply.service.handlers.excel.export;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.stockadjustment.StockAdjustmentAggregationCodesDTO;
import uz.uzkassa.smartpossupply.filters.StockAdjustmentFilter;
import uz.uzkassa.smartpossupply.service.StockAdjustmentService;

import java.util.LinkedList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StockAdjustmentAggregationCodesExcelHandler extends AbstractExcelHandler<StockAdjustmentFilter> {

    StockAdjustmentService stockAdjustmentService;

    @Override
    public String getFileName(StockAdjustmentFilter filter) {
        return "Stock_Adjustment_Aggregation_Codes.xlsx";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("Markirovka kodlari ro'yxati\n(Список кодов маркировки)");
        header.add("№");
        header.add("MXIK\n(ИКПУ)");
        header.add("Markirovka kodi\n(Код маркировки)");
        return header;
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(StockAdjustmentFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        this.createHeaders(getHeaders(), sheet);
        List<StockAdjustmentAggregationCodesDTO> adjustmentAggregationCodes = stockAdjustmentService.getCodes(filter.getId());
        int rowIndex = 1;
        for (StockAdjustmentAggregationCodesDTO stockAdjustmentAggregationCodesDTO : adjustmentAggregationCodes) {
            for (String code : stockAdjustmentAggregationCodesDTO.getCodes()) {
                Row row = sheet.createRow(rowIndex + 1);
                row.createCell(0).setCellValue(rowIndex++);
                row.createCell(1).setCellValue(stockAdjustmentAggregationCodesDTO.getVatBarcode());
                row.createCell(2).setCellValue(code);
            }
        }
        return workbook;
    }

    private void createHeaders(List<String> cellNames, XSSFSheet sheet) {
        XSSFWorkbook workbook = sheet.getWorkbook();
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setWrapText(true);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Arial");
        headerStyle.setFont(font);

        // First row
        Row headerRow = sheet.createRow(0);
        headerRow.setHeightInPoints(30);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue(cellNames.get(0));
        headerCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(CellRangeAddress.valueOf("A1:C1"));
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);

        // Second row
        Row colHeader = sheet.createRow(1);
        colHeader.setHeightInPoints(30);

        Cell cellNumber = colHeader.createCell(0);
        cellNumber.setCellValue(cellNames.get(1));
        cellNumber.setCellStyle(headerStyle);

        Cell cellVatBarcode = colHeader.createCell(1);
        cellVatBarcode.setCellValue(cellNames.get(2));
        cellVatBarcode.setCellStyle(headerStyle);

        Cell cellCode = colHeader.createCell(2);
        cellCode.setCellValue(cellNames.get(3));
        cellCode.setCellStyle(headerStyle);
    }
}
