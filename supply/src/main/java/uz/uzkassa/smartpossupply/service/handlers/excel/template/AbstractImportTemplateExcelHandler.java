package uz.uzkassa.smartpossupply.service.handlers.excel.template;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.FileConstants;
import uz.uzkassa.smartpossupply.constants.ImportConstants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 31.03.2023
 */
@Component
public abstract class AbstractImportTemplateExcelHandler implements ImportConstants {

    protected XSSFWorkbook getXSSFWorkbook(String name, List<String> headers) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(name);
        createHeader(workbook, headers);
        sheet.createFreezePane(0, 1);
        setColumnWidth(sheet, headers.size());
        return workbook;
    }

    protected void createHeader(XSSFWorkbook wb, List<String> headers) {
        CellStyle headerStyle = getHeaderStyle(wb);
        XSSFSheet sheet = wb.getSheetAt(0);
        XSSFRow headerRow = sheet.createRow(0);
        int i = 0;
        for (String header : headers) {
            XSSFCell cell = headerRow.createCell(i++);
            cell.setCellValue(header);
            cell.setCellType(CellType.STRING);
            cell.setCellStyle(headerStyle);
        }
    }

    protected CellStyle getHeaderStyle(XSSFWorkbook wb) {
        Font headerFont = wb.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 13);
        headerFont.setColor(IndexedColors.BLUE.getIndex());
        CellStyle headerCellStyle = wb.createCellStyle();
        headerCellStyle.setFont(headerFont);
        return headerCellStyle;
    }

    private void setColumnWidth(XSSFSheet sheet, int headerSize) {
        for (int i = 0; i < headerSize; i++) {
            sheet.setColumnWidth(i, 7000);
        }
    }

    protected byte[] getResource(XSSFWorkbook workbook) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
            bos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bos.toByteArray();
    }

    ResponseEntity<Resource> getTemplate(XSSFWorkbook workbook, String fileName) {
        try {
            Resource resource = new ByteArrayResource(getResource(workbook));
            return ResponseEntity
                .ok()
                .header("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + ".xlsx")
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType(FileConstants.CONTENT_TYPE_FOR_EXCEL))
                .body(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
