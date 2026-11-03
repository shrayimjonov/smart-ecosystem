package uz.uzkassa.smartpos.report;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import uz.uzkassa.smartpos.repository.clickhouse.receipt.ClickhouseReceiptGenericRepository;
import uz.uzkassa.smartpos.repository.clickhouse.receiptdetail.ClickhouseReceiptDetailRepository;
import uz.uzkassa.smartpos.repository.postgres.company.BranchRepository;
import uz.uzkassa.smartpos.repository.postgres.receipt.ShiftReportRepository;
import uz.uzkassa.smartpos.service.SmartposService;

@RequiredArgsConstructor
public abstract class AbstractExcelReport extends SmartposService {
    @Autowired
    @Lazy
    protected ClickhouseReceiptGenericRepository clickhouseReceiptGenericRepository;
    @Autowired
    @Lazy
    protected ClickhouseReceiptDetailRepository clickhouseReceiptDetailRepository;
    @Autowired
    @Lazy
    protected ShiftReportRepository shiftReportRepository;
    @Autowired
    @Lazy
    protected BranchRepository branchRepository;

    void createHeader(XSSFWorkbook wb, XSSFSheet sheet, String[] headerTitles) {
        createHeader(wb, sheet, headerTitles, 0);
    }

    void createHeader(XSSFWorkbook wb, XSSFSheet sheet, String[] headerTitles, int headerRowIndex) {
        CellStyle headerCellStyle = getHeaderStyle(wb);
        XSSFRow headerRow = sheet.createRow(headerRowIndex);

        for (int i = 0; i < headerTitles.length; i++) {
            sheet.autoSizeColumn(i);
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headerTitles[i]);
            cell.setCellStyle(headerCellStyle);
        }
    }

    protected CellStyle getHeaderStyle(XSSFWorkbook wb) {
        // Create a Font for styling header cells
        Font headerFont = wb.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 13);
        headerFont.setColor(IndexedColors.BLUE.getIndex());

        // Create a CellStyle with the font
        CellStyle headerCellStyle = wb.createCellStyle();
        headerCellStyle.setFont(headerFont);
        return headerCellStyle;
    }

    protected CellStyle getSummaryStyle(XSSFWorkbook wb) {
        // Create a Font for styling header cells
        Font headerFont = wb.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);

        // Create a CellStyle with the font
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(wb.createDataFormat().getFormat("0.000"));
//        cellStyle.setDataFormat((short) 3);
        cellStyle.setFont(headerFont);
        return cellStyle;
    }

}
