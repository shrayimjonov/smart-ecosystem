package uz.uzkassa.smartpossupply.service.handlers.excel.export;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationHistoryExportDTO;
import uz.uzkassa.smartpossupply.utils.DateUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;


@Component
@Slf4j
public class AggregationHistoryExportExcelHandler {

    private static final int ROW_ACCESS_WINDOW = 500;
    private static final int COLUMN_WIDTH = 7000;

    public String getFileName() {
        return "Aggregation_History_" + LocalDate.now() + ".xlsx";
    }

    public LinkedList<String> getHeaders() {
        LinkedList<String> headers = new LinkedList<>();
        headers.add("Код");
        headers.add("Штрихкод");
        headers.add("Название товара");
        headers.add("Дата производства");
        headers.add("Тип упаковки");
        headers.add("Кому принадлежит");
        headers.add("Завод изготовитель");
        headers.add("Статус");
        return headers;
    }

    public byte[] generate(List<AggregationHistoryExportDTO> items) {
        SXSSFWorkbook workbook = new SXSSFWorkbook(ROW_ACCESS_WINDOW);
        try {
            SXSSFSheet sheet = workbook.createSheet("Aggregation history");
            createHeader(workbook, sheet);
            int rowIndex = 0;
            for (AggregationHistoryExportDTO item : items) {
                Row row = sheet.createRow(++rowIndex);
                row.createCell(0).setCellValue(text(item.getCode()));
                row.createCell(1).setCellValue(text(item.getGtin()));
                row.createCell(2).setCellValue(text(item.getProductName()));
                row.createCell(3).setCellValue(date(item.getProducedDate()));
                row.createCell(4).setCellValue(text(item.getPackageType()));
                row.createCell(5).setCellValue(text(item.getOwnerName()));
                row.createCell(6).setCellValue(text(item.getProducerName()));
                row.createCell(7).setCellValue(text(item.getStatus()));
            }
            return getResource(workbook);
        } finally {
            // drops the temp files SXSSF spilled the flushed rows into
            workbook.dispose();
            try {
                workbook.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private void createHeader(SXSSFWorkbook workbook, SXSSFSheet sheet) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Arial");
        headerStyle.setFont(font);

        Row row = sheet.createRow(0);
        int columnIndex = 0;
        for (String header : getHeaders()) {
            Cell cell = row.createCell(columnIndex);
            cell.setCellValue(header);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(columnIndex, COLUMN_WIDTH);
            columnIndex++;
        }
        sheet.createFreezePane(0, 1);
    }

    private byte[] getResource(SXSSFWorkbook workbook) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            workbook.write(bos);
            return bos.toByteArray();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BadRequestException("An unexpected error has occurred while exporting data", e.getMessage());
        }
    }

    private String text(String value) {
        return value != null ? value : "";
    }

    private String date(LocalDateTime value) {
        return value != null ? value.format(DateUtils.DAY_MONTH_YEAR_DOT_FORMATTER) : "";
    }
}
