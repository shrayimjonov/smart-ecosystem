package uz.uzkassa.smartpos.utils;

import org.apache.poi.ss.usermodel.*;

public class ExcelUtils {
    public static Cell cell(Row row, int i, Workbook workbook, boolean bold, boolean center, boolean border) {
        Cell cell = row.createCell(i);
        Font font = workbook.createFont();
        font.setBold(bold);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        if (center) {
            style.setAlignment(HorizontalAlignment.CENTER);
        }
        if (border) {
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
        }
        cell.setCellStyle(style);
        return cell;
    }




}
