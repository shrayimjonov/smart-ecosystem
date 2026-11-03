package uz.uzkassa.silen.excel;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import uz.uzkassa.silen.dto.MaterialReportDTO;
import uz.uzkassa.silen.dto.MaterialReportFilter;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.service.AggregationHistoryService;

import java.io.IOException;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;


@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MaterialReportExcelHandler {
    final AggregationHistoryService aggregationHistoryService;
    boolean hasBox = false;
    boolean hasPallet = false;
    int addColumn;
    int maxColumn;
    CellStyle cellStyle;
    CellStyle numberStyle;
    private XSSFWorkbook workbook;
    private XSSFSheet workingSheet;
    private XSSFFormulaEvaluator evaluator;
    private int numberOfElements;

    private void init() {
        workbook = new XSSFWorkbook();
        workingSheet = workbook.createSheet("Материальный отчет");
        cellStyle = getHeaderStyle(workbook);
        numberStyle = getNumberStyle();
        evaluator = new XSSFFormulaEvaluator(workbook);
    }

    public void write(MaterialReportFilter filter, OutputStream outputStream) {
        init();
        hasBox = filter.getPackageTypes().contains(PackageType.BOX);
        hasPallet = filter.getPackageTypes().contains(PackageType.PALLET);
        addColumn = (hasBox ? 1 : 0) + (hasPallet ? 1 : 0);
        rangeColumns();
        workingSheet.setHorizontallyCenter(true);
        workingSheet.setVerticallyCenter(true);
        writeHeaders(filter.getFrom().format(DateTimeFormatter.ISO_DATE), filter.getFrom().format(DateTimeFormatter.ISO_DATE));
        writeBody(filter);

        try {
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void rangeColumns() {
        maxColumn = 10 + addColumn * 4;
        workingSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, maxColumn));
        for (int i = 2; i < maxColumn; i += 2 + addColumn) {
            int start = i + 1 + addColumn;
            workingSheet.addMergedRegion(new CellRangeAddress(1, 1, i, start));
        }
    }

    private void writeBody(MaterialReportFilter filter) {
        Page<MaterialReportDTO> page = aggregationHistoryService.materialReport(filter);
        numberOfElements = page.getNumberOfElements();
        int rowIndex = 3;
        for (MaterialReportDTO dto : page) {
            int cellIndex = 0;
            Row row = workingSheet.createRow(rowIndex++);
            row.createCell(cellIndex++).setCellValue(cellIndex);
            row.createCell(cellIndex).setCellValue(dto.getProductName());
            cellIndex = createAdditionalCell(true, row, cellIndex, dto.getPrice().doubleValue());
            cellIndex = createAdditionalCell(true, row, cellIndex, dto.getBalanceStartBottle().doubleValue());
            cellIndex = createAdditionalCell(hasBox, row, cellIndex, dto.getBalanceStartBox().doubleValue());
            cellIndex = createAdditionalCell(hasPallet, row, cellIndex, dto.getBalanceStartPallet().doubleValue());
            cellIndex = createAdditionalCell(true, row, cellIndex, dto.getBalanceStartBottleSum().doubleValue());

            cellIndex = createAdditionalCell(true, row, cellIndex, dto.getProductionBottle().doubleValue());
            cellIndex = createAdditionalCell(hasBox, row, cellIndex, dto.getProductionBox().doubleValue());
            cellIndex = createAdditionalCell(hasPallet, row, cellIndex, dto.getProductionPallet().doubleValue());
            cellIndex = createAdditionalCell(true, row, cellIndex, dto.getProductionBottleSum().doubleValue());

            cellIndex = createAdditionalCell(true, row, cellIndex, dto.getShippedBottle().doubleValue());
            cellIndex = createAdditionalCell(hasBox, row, cellIndex, dto.getShippedBox().doubleValue());
            cellIndex = createAdditionalCell(hasPallet, row, cellIndex, dto.getShippedPallet().doubleValue());
            cellIndex = createAdditionalCell(true, row, cellIndex, dto.getShippedBottleSum().doubleValue());

            cellIndex = createAdditionalCell(true, row, cellIndex, dto.getBalanceEndBottle().doubleValue());
            cellIndex = createAdditionalCell(hasBox, row, cellIndex, dto.getBalanceEndBox().doubleValue());
            cellIndex = createAdditionalCell(hasPallet, row, cellIndex, dto.getBalanceEndPallet().doubleValue());
            createAdditionalCell(true, row, cellIndex, dto.getBalanceEndBottleSum().doubleValue());
        }
        addSummaryRow(rowIndex);
    }

    private void addSummaryRow(int rowIndex) {
        char[] letters = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I','J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q'};
        Row summaryRow = workingSheet.createRow(rowIndex);
        int cellIndex = 0;
        summaryRow.createCell(cellIndex++).setCellValue("Итог");
        //getPrice
        cellIndex = createFormula(true, summaryRow, cellIndex, letters[cellIndex]);
        //getBalanceStartBottle
        cellIndex = createFormula(true, summaryRow, cellIndex, letters[cellIndex]);
        //getBalanceStartBox
        cellIndex = createFormula(hasBox, summaryRow, cellIndex, letters[cellIndex]);
        //getBalanceStartPallet
        cellIndex = createFormula(hasPallet, summaryRow, cellIndex, letters[cellIndex]);
        //getBalanceStartBottleSum
        cellIndex = createFormula(true, summaryRow, cellIndex, letters[cellIndex]);
        //getProductionBottle
        cellIndex = createFormula(true, summaryRow, cellIndex, letters[cellIndex]);
        //getProductionBox
        cellIndex = createFormula(hasBox, summaryRow, cellIndex, letters[cellIndex]);
        //getProductionPallet
        cellIndex = createFormula(hasPallet, summaryRow, cellIndex, letters[cellIndex]);
        //getProductionBottleSum
        cellIndex = createFormula(true, summaryRow, cellIndex, letters[cellIndex]);
        //getShippedBottle
        cellIndex = createFormula(true, summaryRow, cellIndex, letters[cellIndex]);
        //getShippedBox
        cellIndex = createFormula(hasBox, summaryRow, cellIndex, letters[cellIndex]);
        //getShippedPallet
        cellIndex = createFormula(hasPallet, summaryRow, cellIndex, letters[cellIndex]);
        //getShippedBottleSum
        cellIndex = createFormula(true, summaryRow, cellIndex, letters[cellIndex]);
        //getBalanceEndBottle
        cellIndex = createFormula(true, summaryRow, cellIndex, letters[cellIndex]);
        //getBalanceEndBox
        cellIndex = createFormula(hasBox, summaryRow, cellIndex, letters[cellIndex]);
        //getBalanceEndPallet
        cellIndex = createFormula(hasPallet, summaryRow, cellIndex, letters[cellIndex]);
        //getBalanceEndBottleSum
        createFormula(true, summaryRow, cellIndex, letters[cellIndex]);
    }

    private int createFormula(boolean isAddColumn, Row row, int cellIndex, char letter) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);

        if (isAddColumn) {
            Cell cell = row.createCell(cellIndex++);
            String formula = "SUM(" + letter + "4:" + letter + (numberOfElements + 3) + ")";
            cell.setCellFormula(formula);
            cell.setCellStyle(style);
            evaluator.evaluate(cell);
        }
        return cellIndex;
    }

    private int createAdditionalCell(boolean isAddColumn, Row row, int cellIndex, Double dto) {
        if (isAddColumn) {
            Cell cell = row.createCell(cellIndex++);
            cell.setCellValue(dto);
            cell.setCellStyle(numberStyle);
        }
        return cellIndex;
    }

    private void writeHeaders(String from, String to) {
        writeFirstRow();
        writeSecondRow(from, to);
        writeThirdRow();

//        workingSheet.setColumnWidth(1, 90 * 100);
        for (int i = 0; i < maxColumn; i++) {
            workingSheet.setColumnWidth(i, 120 * 45);
        }
    }

    private void writeFirstRow() {
        XSSFRow firstRow = workingSheet.createRow(0);
        XSSFCell cell = firstRow.createCell(0);
        cell.setCellValue("Материальный отчет");
        cell.setCellStyle(cellStyle);
    }

    private void writeSecondRow(String from, String to) {
        int cellIndex = 0;
        XSSFRow headerRow2 = workingSheet.createRow(1);
        Cell cell = headerRow2.createCell(cellIndex++);
        cell.setCellValue("ТМЦ");
        cell.setCellStyle(cellStyle);

        Cell cell1 = headerRow2.createCell(cellIndex++);
        cell1.setCellValue("Цена");
        cell1.setCellStyle(cellStyle);

        Cell cell2 = headerRow2.createCell(cellIndex);
        cell2.setCellValue("Сальдо на " + from);
        cell2.setCellStyle(cellStyle);

        cellIndex = cellIndex + 2 + addColumn;
        Cell cell4 = headerRow2.createCell(cellIndex);
        cell4.setCellValue("Производство");
        cell4.setCellStyle(cellStyle);

        cellIndex = cellIndex + 2 + addColumn;
        Cell cell5 = headerRow2.createCell(cellIndex);
        cell5.setCellValue("Реализация");
        cell5.setCellStyle(cellStyle);

        cellIndex = cellIndex + 2 + addColumn;
        Cell cell6 = headerRow2.createCell(cellIndex);
        cell6.setCellValue("Сальдо на " + to + " Остаток");
        cell6.setCellStyle(cellStyle);
    }

    private void writeThirdRow() {
        int cellIndex = 2;
        XSSFRow headerRow3 = workingSheet.createRow(cellIndex);
        cellIndex = reportCount(cellIndex, headerRow3);
        cellIndex = reportCount(cellIndex, headerRow3);
        cellIndex = reportCount(cellIndex, headerRow3);
        reportCount(cellIndex, headerRow3);
    }

    private int reportCount(int cellIndex, XSSFRow headerRow3) {
        cellIndex = createHeaderCell("Кол-во", cellIndex, headerRow3);
        if (hasBox) {
            cellIndex = createHeaderCell("Кол-во (Коробка)", cellIndex, headerRow3);
        }
        if (hasPallet) {
            cellIndex = createHeaderCell("Кол-во (Паллет)", cellIndex, headerRow3);
        }
        return createHeaderCell("Сумма", cellIndex, headerRow3);
    }

    private int createHeaderCell(String value, int cellIndex, XSSFRow headerRow3) {
        Cell cell = headerRow3.createCell(cellIndex++);
        cell.setCellValue(value);
        cell.setCellStyle(cellStyle);
        return cellIndex;
    }


    public CellStyle getHeaderStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // create font
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        // style alignment
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        return style;
    }

    public CellStyle getNumberStyle() {
        // number style format
        CellStyle numberStyle = workbook.createCellStyle();
        numberStyle.setDataFormat((short) 2);
        return numberStyle;
    }
}
