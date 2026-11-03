package uz.uzkassa.cts.handlers.export.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.constants.FileConstants;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
@Slf4j
public abstract class AbstractExportExcelHandler<T extends BaseFilter> implements FileConstants {

    public abstract String getFileName(T filter);

    protected abstract List<String> getHeaders();

    protected abstract XSSFWorkbook getXSSFWorkbook(T filter);

    public byte[] generate(T filter) {
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        XSSFWorkbook workbook = getXSSFWorkbook(filter);
        XSSFSheet sheet = workbook.getSheetAt(0);
        for (int i = 0; i < sheet.getRow(0).getPhysicalNumberOfCells(); i++) {
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
        XSSFWorkbook workbook = sheet.getWorkbook();
        Row row = sheet.createRow(0);
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
