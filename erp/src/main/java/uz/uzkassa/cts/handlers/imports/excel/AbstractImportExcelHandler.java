package uz.uzkassa.cts.handlers.imports.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.constants.ImportConstants;
import uz.uzkassa.cts.dto.payload.ImportReportItemPayload;
import uz.uzkassa.cts.dto.payload.ImportReportPayload;
import uz.uzkassa.cts.repository.ImportReportItemRepository;
import uz.uzkassa.cts.repository.ImportReportRepository;

import java.util.List;


@Component
public abstract class AbstractImportExcelHandler implements ImportConstants {

    @Autowired
    protected ImportReportRepository importReportRepository;

    @Autowired
    protected ImportReportItemRepository importReportItemRepository;

    protected abstract List<ImportReportItemPayload> readFile(MultipartFile file);

    protected abstract void importReportQueue(ImportReportPayload importReportPayload);

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
}
