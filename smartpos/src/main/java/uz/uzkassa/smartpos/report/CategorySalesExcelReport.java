package uz.uzkassa.smartpos.report;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.dto.CategoryDTO;
import uz.uzkassa.smartpos.dto.filter.receipt.SalesStatsFilter;
import uz.uzkassa.smartpos.service.AnalyticsService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class CategorySalesExcelReport extends AbstractExcelReport {
    private final AnalyticsService analyticsService;

    public static final String[] HEADER_TITLES = new String[]{"Наименование", "Кол-во", "Сумма", "НДС", "Кол-во чеков"};

    public ByteArrayResource getExcelReport(SalesStatsFilter filter) throws IOException {
        List<String> mainGroupRowPosition = new ArrayList<>();
        try (XSSFWorkbook wb = new XSSFWorkbook(); var bos = new ByteArrayOutputStream()) {
            XSSFSheet sheet = wb.createSheet("Отчет");

            createHeader(wb, sheet, HEADER_TITLES, 0);
            AtomicInteger startRow = new AtomicInteger(1);
            createBody(sheet, analyticsService.getCategorySalesStats(filter), startRow, 0, mainGroupRowPosition);

            setColumnWidth(sheet);
            sheet.setRowSumsBelow(false);
            createSummaryRow(sheet, startRow.get(), mainGroupRowPosition);
            wb.write(bos);
            return new ByteArrayResource(bos.toByteArray());
        }

    }

    protected void createBody(XSSFSheet sheet, List<CategoryDTO> categoryDTOS, AtomicInteger r, Integer depth, List<String> mainGroupRowPosition) {
        for (CategoryDTO category : categoryDTOS) {

            XSSFRow row = sheet.createRow(r.getAndIncrement());
            int start = r.get();
            if (category.getParentId() == null) {
                mainGroupRowPosition.add(Integer.toString(start));
            }
            int c = 0;
            XSSFCell cell = row.createCell(c++);
            cell.setCellValue(padLeft(category.getName(), depth));

            cell = row.createCell(c++);
            cell.setCellValue(category.getSalesStats().getSalesCount().doubleValue());

            cell = row.createCell(c++);
            cell.setCellValue(category.getSalesStats().getSalesTotal().doubleValue());

            cell = row.createCell(c++);
            cell.setCellValue(category.getSalesStats().getNds() != null ? category.getSalesStats().getNds().doubleValue() : 0);

            cell = row.createCell(c);
            cell.setCellValue(category.getSalesStats().getReceiptCount() != null ? category.getSalesStats().getReceiptCount() : 0);

            createBody(sheet, category.getChildren(), r, depth + 1, mainGroupRowPosition);
            if (depth < 6 && !category.getChildren().isEmpty()) {
                sheet.groupRow(start, r.get() - 1);
            }
        }
    }

    private void createSummaryRow(XSSFSheet sheet, int r, List<String> mainGroupRowPosition) {
        XSSFRow row = sheet.createRow(r);
        XSSFCell cell = row.createCell(0);
        cell.setCellValue("Итог");
        cell.setCellStyle(getSummaryStyle(sheet.getWorkbook()));

        String formula = "SUM(B" + formulaFormat("B", mainGroupRowPosition) + ")";
        cell = row.createCell(1);
        cell.setCellFormula(formula);
        cell.setCellStyle(getSummaryStyle(sheet.getWorkbook()));

        formula = "SUM(C" + formulaFormat("C", mainGroupRowPosition) + ")";
        cell = row.createCell(2);
        cell.setCellFormula(formula);
        cell.setCellStyle(getSummaryStyle(sheet.getWorkbook()));

        formula = "SUM(D" + formulaFormat("D", mainGroupRowPosition) + ")";
        cell = row.createCell(3);
        cell.setCellFormula(formula);
        cell.setCellStyle(getSummaryStyle(sheet.getWorkbook()));

        formula = "SUM(E" + formulaFormat("E", mainGroupRowPosition) + ")";
        cell = row.createCell(4);
        cell.setCellFormula(formula);
        cell.setCellStyle(getSummaryStyle(sheet.getWorkbook()));
    }

    private String padLeft(String str, Integer depth) {
        StringBuilder strBuilder = new StringBuilder(str);
        for (int i = 0; i < depth; i++) {
            strBuilder.insert(0, "   ");
        }
        str = strBuilder.toString();
        return str;
    }

    private String formulaFormat(String columnName, List<String> mainGroupRowPosition) {
        if (mainGroupRowPosition.size() > 1) {
            return mainGroupRowPosition.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", " + columnName));
        } else
            return mainGroupRowPosition.getFirst();
    }

    private void setColumnWidth(XSSFSheet sheet) {
        sheet.setColumnWidth(0, 8000);//"Наименование"
        sheet.setColumnWidth(1, 4000);//"Кол-во"
        sheet.setColumnWidth(2, 4000);//Продано на сумму
        sheet.setColumnWidth(3, 4000);//НДС
        sheet.setColumnWidth(4, 5000);//Кол-во чеков
    }
}
