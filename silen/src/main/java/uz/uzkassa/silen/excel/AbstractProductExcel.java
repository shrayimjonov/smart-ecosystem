package uz.uzkassa.silen.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;

@Component
@Slf4j
public abstract class AbstractProductExcel {
    String[] HEADER_TITLES;
    int[] COLUMN_WIDTH;
    String SHEET_NAME = "Abstract excel sheet";
    XSSFWorkbook workbook;
    XSSFSheet workingSheet;

    public abstract void writeBody();

    /**
     * setHeaderTitles(
     * "ИНН", "НАЗВАНИЕ ПРОДУКТА",
     * "КОРОТКОЕ НАЗВАНИЕ ПРОДУКТА",
     * "ИКПУ", "ЕД. ИЗМЕРЕНИЯ",
     * "НДС (СТАВКА %)", "ЦЕНА", "ТАРА",
     * "GTIN", "ТИП ПРОДУКЦИИ",
     * "ОБЪЕМ", "ГРАДУС"
     * };
     */
    public void setHeaderTitles(String... headerTitles) {
        this.HEADER_TITLES = headerTitles;
    }

    /**
     * setColumnWidth(70, 130, 130, 130, 100, 60, 80, 80, 100, 180, 60, 60);
     */
    public void setColumnWidth(int... columnWidth) {
        this.COLUMN_WIDTH = columnWidth;
    }

    /**
     * setWorkingSheetName("Working sheet name");
     */
    public void setWorkingSheetName(String workingSheetName) {
        this.SHEET_NAME = workingSheetName;
    }

    /**
     * <ol>
     * <li>create XSSFWorkbook</li>
     * <li>create Sheet</li>
     * <li>call createHeaderStyle()</li>
     * <li>call writeHeaders()</li>
     * </ol>
     * <p>
     * for readFromFile or from stream
     * init(InputStream fromReadInputStream, null);
     * <p>
     *     <p>
     * for writeToStream or toFile
     * init(null, response.getOutputStream());
     * </p>
     */
    public void execute(OutputStream toWriteOutputStream) {

        workbook = new XSSFWorkbook();
        workingSheet = workbook.createSheet(SHEET_NAME);
        writeHeaders();
        writeBody();
        try {
            contentWriteTo(toWriteOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void contentWriteTo(OutputStream outputStream) throws IOException {
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    public void contentReadFrom(InputStream inputStream) throws IOException {
        //Create Workbook instance holding reference to .xls file
        workbook = new XSSFWorkbook(inputStream);
        //Get first/desired sheet from the workbook
        workingSheet = workbook.getSheetAt(0);
    }

    public void setHeaderColumnWidth() {
        for (int i = 0; i < HEADER_TITLES.length; i++) {
            workingSheet.setColumnWidth(i, COLUMN_WIDTH[i] * 36);
        }
    }

    public CellStyle getHeaderStyle() {
        CellStyle headerStyle = workbook.createCellStyle();
        // create font
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        headerStyle.setFont(font);
        // style alignment
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setWrapText(true);
        return headerStyle;
    }

    public CellStyle getNumberStyle() {
        // number style format
        CellStyle numberStyle = workbook.createCellStyle();
        numberStyle.setDataFormat((short) 2);
        return numberStyle;
    }

    public CellStyle getPercentStyle() {
        // number style format
        CellStyle percentStyle = workbook.createCellStyle();
//        String s = BuiltinFormats.getBuiltinFormat(9);
        percentStyle.setDataFormat((short) 9);
        return percentStyle;
    }

    public CellStyle getStringStyle() {
        //string style format
        CellStyle stringStyle = workbook.createCellStyle();
        DataFormat stringFmt = workbook.createDataFormat();
        stringStyle.setDataFormat(stringFmt.getFormat("@"));
        return stringStyle;
    }

    public void writeHeaders() {
        CellStyle headerCellStyle = getHeaderStyle();
        XSSFRow headerRow = workingSheet.createRow(0);

        for (int i = 0; i < this.HEADER_TITLES.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(this.HEADER_TITLES[i]);
            cell.setCellStyle(headerCellStyle);
        }
        setHeaderColumnWidth();
    }

    /**
     * @param row    int
     * @param column int
     */
    public void setDropdownListTo(int row, int column, String... values) {
        DataValidationHelper validationHelper = new XSSFDataValidationHelper(workingSheet);
        DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(values);
        CellRangeAddressList addressList = new CellRangeAddressList(row, row, column, column);
        DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);
        dataValidation.setEmptyCellAllowed(false);
        dataValidation.setSuppressDropDownArrow(true);
        dataValidation.setShowErrorBox(true);
        workingSheet.addValidationData(dataValidation);
    }

    public String convertToString(final Cell cell) {
        if (cell.getCellType() == CellType.NUMERIC)
            return String.valueOf(cell.getNumericCellValue());
        return cell.getStringCellValue();
    }

    public BigDecimal convertToBig(Cell cell) {

        if (cell.getCellType().equals(CellType.STRING)) {
            return BigDecimal.valueOf(Double.parseDouble(cell.getStringCellValue().replace(",", ".")));
        } else if (cell.getCellType().equals(CellType.NUMERIC))
            return BigDecimal.valueOf(cell.getNumericCellValue());
        else if (cell.getCellType().equals(CellType.BLANK))
            return BigDecimal.valueOf(cell.getNumericCellValue());

        return BigDecimal.valueOf(cell.getNumericCellValue());
    }

    public Double convertToDouble(Cell cell) {
        if (cell.getCellType().equals(CellType.STRING))
            return Double.parseDouble(cell.getStringCellValue());
        return cell.getNumericCellValue();
    }
}
