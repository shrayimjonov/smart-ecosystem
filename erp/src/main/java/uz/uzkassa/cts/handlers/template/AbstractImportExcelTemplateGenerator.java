package uz.uzkassa.cts.handlers.template;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.constants.FileConstants;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.IntStream;


@Component
@Slf4j
public abstract class AbstractImportExcelTemplateGenerator {

    protected ResponseEntity<Resource> generateTemplate(Workbook workbook, String name) {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        try {
            workbook.write(byteOutputStream);
            byteOutputStream.close();

            Resource resource = new ByteArrayResource(byteOutputStream.toByteArray());
            return ResponseEntity
                    .ok()
                    .header("Content-Disposition", "attachment; filename=" + URLEncoder.encode(name, StandardCharsets.UTF_8) + ".xlsx")
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.parseMediaType(FileConstants.CONTENT_TYPE_FOR_EXCEL))
                    .body(resource);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    protected XSSFWorkbook getXSSFWorkbook(String name, List<String> headers) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(name);
        createHeader(workbook, headers);
        sheet.createFreezePane(0, 1);
        setColumnWidth(sheet, headers.size());
        return workbook;

    }

    private void createHeader(XSSFWorkbook workbook, List<String> headers) {
        CellStyle headerStyle = getHeaderStyle(workbook);
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFRow headerRow = sheet.createRow(0);
        int i = 0;
        for (String header : headers) {
            XSSFCell cell = headerRow.createCell(i++);
            cell.setCellValue(header);
            cell.setCellType(CellType.STRING);
            cell.setCellStyle(headerStyle);
        }
    }

    private CellStyle getHeaderStyle(XSSFWorkbook workbook) {
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 13);
        headerFont.setColor(IndexedColors.RED1.getIndex());
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        return headerCellStyle;
    }

    private void setColumnWidth(XSSFSheet sheet, int headerSize) {
        IntStream.range(0, headerSize).forEach(i -> sheet.setColumnWidth(i, 7000));
    }
}
