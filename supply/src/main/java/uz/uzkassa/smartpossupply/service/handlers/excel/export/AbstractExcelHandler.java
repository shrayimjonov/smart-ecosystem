package uz.uzkassa.smartpossupply.service.handlers.excel.export;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.Constants;
import uz.uzkassa.smartpossupply.filters.SimpleFilter;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

@Component
@Slf4j
public abstract class AbstractExcelHandler<T extends SimpleFilter> {

    protected abstract String getFileName(T filter);

    protected abstract LinkedList<String> getHeaders();

    protected abstract XSSFWorkbook getXSSFWorkbook(T filter);

    public byte[] generate(T filter) {
        filter.setPage(0);
        filter.setSize(Constants.EXCEL_EXPORT_MAX_LIMIT);
        XSSFWorkbook workbook = getXSSFWorkbook(filter);
        XSSFSheet sheet = workbook.getSheetAt(0);
        for (int i = 0; i < getHeaders().size(); i++) {
            sheet.autoSizeColumn(i);
        }
        CellStyle rowStyle = workbook.createCellStyle();
        rowStyle.setAlignment(HorizontalAlignment.CENTER);
        rowStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 16);
        font.setFontName("Arial");
        font.setColor(IndexedColors.BLACK.getIndex());
        rowStyle.setFont(font);
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);
            row.setRowStyle(rowStyle);
        }
        sheet.createFreezePane(0, 1);
        return getResource(workbook);
    }

    protected byte[] getResource(XSSFWorkbook workbook) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BadRequestException("An unexpected error has occurred while exporting data", e.getMessage());
        }
    }

    protected static XSSFWorkbook createWorkbook() {
        return new XSSFWorkbook();
    }

    protected static XSSFSheet createSheet(XSSFWorkbook workbook, String sheetName) {
        return workbook.createSheet(sheetName);
    }

    protected static void createHeader(List<String> cellNames, XSSFSheet sheet) {
        createHeader(cellNames, sheet, 0);
    }

    protected static void createHeader(List<String> cellNames, XSSFSheet sheet, int rowNum) {
        XSSFWorkbook workbook = sheet.getWorkbook();
        Row row = sheet.createRow(rowNum);
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Arial");
        headerStyle.setFont(font);
        int rowIndex = 0;
        for (String cellName : cellNames) {
            Cell cell = row.createCell(rowIndex);
            cell.setCellValue(cellName);
            cell.setCellStyle(headerStyle);
            rowIndex++;
        }
    }
}
