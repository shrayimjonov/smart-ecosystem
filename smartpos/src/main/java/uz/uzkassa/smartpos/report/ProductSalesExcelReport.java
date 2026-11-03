package uz.uzkassa.smartpos.report;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.dto.analytics.SalesStatsDTO;
import uz.uzkassa.smartpos.dto.filter.receipt.SalesStatsFilter;
import uz.uzkassa.smartpos.security.AuthoritiesConstants;
import uz.uzkassa.smartpos.security.SecurityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
@Slf4j
public class ProductSalesExcelReport extends AbstractExcelReport {
    public static final String[] HEADER_TITLES = new String[]{"Филиал", "Название товара", "Штрих код", "ИКПУ", "Кол-во", "Ед. измерения", "Стоимость", "Продано на сумму", "Кол-во чеков", "Категория", "НДС %", "НДС", "Скидка"};
    public static final String[] HEADER_TITLES_WITHOUT_BRANCH = new String[]{"Название товара", "Штрих код", "ИКПУ", "Кол-во", "Ед. измерения", "Стоимость", "Продано на сумму", "Кол-во чеков", "Категория", "НДС %", "НДС", "Скидка"};

    public ByteArrayResource getExcelReport(SalesStatsFilter filter) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook(); var bos = new ByteArrayOutputStream()) {
            XSSFSheet sheet = wb.createSheet("Отчет по филиалам");

            createHeader(wb, sheet, HEADER_TITLES);
            createBody(filter, sheet, true);
            setColumnWidth(sheet);


            sheet = wb.createSheet("Отчет по компаниям");
            createHeader(wb, sheet, HEADER_TITLES_WITHOUT_BRANCH);
            createBody(filter, sheet, false);
            setColumnWidth(sheet);

            wb.write(bos);
            return new ByteArrayResource(bos.toByteArray());
        }
    }


    protected void createBody(SalesStatsFilter filter, XSSFSheet sheet, boolean byBranch) {
        filter.setCompanyId(SecurityUtils.getCurrentCompanyId());
        if (filter.getBranchId() == null && !SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.OWNER)) {
            filter.setBranchId(SecurityUtils.getCurrentBranchId());
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIdsWithBranchId()));
        Page<SalesStatsDTO> page;
        if (byBranch) {
            page = clickhouseReceiptDetailRepository.getProductSalesStatsByBranch(filter, true);
        } else {
            page = clickhouseReceiptDetailRepository.getProductSalesStatsByCompany(filter, true);
        }
        XSSFWorkbook workbook = sheet.getWorkbook();
        CellStyle decimalCellStyle = workbook.createCellStyle();
        decimalCellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.000"));
        int r = 1;
        for (SalesStatsDTO salesStats : page.getContent()) {
            XSSFRow row = sheet.createRow(r++);
            int c = 0;
            XSSFCell cell;
            if (byBranch) {
                cell = row.createCell(c++);
                cell.setCellValue(salesStats.getBranchName());
            }
            cell = row.createCell(c++);
            cell.setCellValue(salesStats.getName());

            cell = row.createCell(c++);
            cell.setCellValue(salesStats.getBarcode());

            cell = row.createCell(c++);
            cell.setCellValue(salesStats.getVatBarcode());

            cell = row.createCell(c++);
            cell.setCellStyle(decimalCellStyle);
            cell.setCellValue(salesStats.getSalesCount().doubleValue());

            cell = row.createCell(c++);
            cell.setCellValue(salesStats.getUnit());

            cell = row.createCell(c++);
            cell.setCellStyle(decimalCellStyle);
            cell.setCellValue(salesStats.getPrice().doubleValue());

            cell = row.createCell(c++);
            cell.setCellStyle(decimalCellStyle);
            cell.setCellValue(salesStats.getSalesTotal().doubleValue());

            cell = row.createCell(c++);
            cell.setCellStyle(decimalCellStyle);
            cell.setCellValue(salesStats.getReceiptCount());

            cell = row.createCell(c++);
            cell.setCellValue(salesStats.getCategory());

            cell = row.createCell(c++);
            cell.setCellStyle(decimalCellStyle);
            cell.setCellValue(salesStats.getNdsPercent().doubleValue());

            cell = row.createCell(c++);
            cell.setCellStyle(decimalCellStyle);
            cell.setCellValue(salesStats.getNds().doubleValue());

            cell = row.createCell(c);
            cell.setCellStyle(decimalCellStyle);
            cell.setCellValue(salesStats.getDiscount().doubleValue());
        }
        if (byBranch) {
            createSummaryRowByBranch(sheet, r);
        } else {
            createSummaryRowByCompany(sheet, r);
        }
    }

    private void createSummaryRowByBranch(XSSFSheet sheet, int r) {
        XSSFRow row = sheet.createRow(r);
        XSSFCell cell = row.createCell(0);
        cell.setCellValue("Итог");
        cell.setCellStyle(getSummaryStyle(sheet.getWorkbook()));

        String formula = "SUM(E2:E" + r + ")";
        cell = row.createCell(4);
        cell.setCellFormula(formula);
        cell.setCellStyle(getSummaryStyle(sheet.getWorkbook()));

        formula = "SUM(G2:G" + r + ")";
        cell = row.createCell(6);
        cell.setCellFormula(formula);
        cell.setCellStyle(getSummaryStyle(sheet.getWorkbook()));

        formula = "SUM(H2:H" + r + ")";
        cell = row.createCell(7);
        cell.setCellFormula(formula);
        cell.setCellStyle(getSummaryStyle(sheet.getWorkbook()));

        formula = "SUM(I2:I" + r + ")";
        cell = row.createCell(8);
        cell.setCellFormula(formula);
        cell.setCellStyle(getSummaryStyle(sheet.getWorkbook()));

        formula = "SUM(L2:L" + r + ")";
        cell = row.createCell(11);
        cell.setCellFormula(formula);
        cell.setCellStyle(getSummaryStyle(sheet.getWorkbook()));

        formula = "SUM(M2:M" + r + ")";
        cell = row.createCell(12);
        cell.setCellFormula(formula);
        cell.setCellStyle(getSummaryStyle(sheet.getWorkbook()));

    }

    private void createSummaryRowByCompany(XSSFSheet sheet, int r) {
        XSSFRow row = sheet.createRow(r);
        XSSFCell cell = row.createCell(0);
        cell.setCellValue("Итог");
        cell.setCellStyle(getSummaryStyle(sheet.getWorkbook()));

        String formula = "SUM(D2:D" + r + ")";
        cell = row.createCell(3);
        cell.setCellFormula(formula);
        cell.setCellStyle(getSummaryStyle(sheet.getWorkbook()));

        formula = "SUM(F2:F" + r + ")";
        cell = row.createCell(5);
        cell.setCellFormula(formula);
        cell.setCellStyle(getSummaryStyle(sheet.getWorkbook()));

        formula = "SUM(G2:G" + r + ")";
        cell = row.createCell(6);
        cell.setCellFormula(formula);
        cell.setCellStyle(getSummaryStyle(sheet.getWorkbook()));

        formula = "SUM(H2:H" + r + ")";
        cell = row.createCell(7);
        cell.setCellFormula(formula);
        cell.setCellStyle(getSummaryStyle(sheet.getWorkbook()));

        formula = "SUM(K2:K" + r + ")";
        cell = row.createCell(10);
        cell.setCellFormula(formula);
        cell.setCellStyle(getSummaryStyle(sheet.getWorkbook()));

        formula = "SUM(L2:L" + r + ")";
        cell = row.createCell(11);
        cell.setCellFormula(formula);
        cell.setCellStyle(getSummaryStyle(sheet.getWorkbook()));

    }

    private void setColumnWidth(XSSFSheet sheet) {
        sheet.setColumnWidth(0, 9000);//"Название товара"
        sheet.setColumnWidth(1, 4000);//"Кол-во"
        sheet.setColumnWidth(2, 6000);//Продано на сумму
        sheet.setColumnWidth(3, 4500);//Стоимость
        sheet.setColumnWidth(4, 5000);//Кол-во чеков
        sheet.setColumnWidth(5, 4700);//"Eд. измерения"
        sheet.setColumnWidth(6, 8000);//"Категория"
        sheet.setColumnWidth(7, 4000);//НДС
        sheet.setColumnWidth(8, 4500);//Скидка
    }
}
