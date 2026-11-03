package uz.uzkassa.smartpos.report;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranchId;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.dto.analytics.SalesStatsDTO;
import uz.uzkassa.smartpos.dto.filter.receipt.SalesDynamicsFilter;
import uz.uzkassa.smartpos.exceptions.EntityNotFound;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductBranchRepository;
import uz.uzkassa.smartpos.service.AnalyticsService;
import uz.uzkassa.smartpos.service.LocalizationService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@AllArgsConstructor
@Component
@Slf4j
public class ProductSalesDynamicExcelReport extends AbstractExcelReport {
    private final ProductBranchRepository productBranchRepository;
    private final LocalizationService localizationService;
    private final AnalyticsService analyticsService;
    public static final String[] HEADER_TITLES = new String[]{"Дата", "Кол-во", "Продано на сумму", "Стоимость", "Кол-во чеков", "НДС", "Скидка"};

    public ByteArrayResource getExcelReport(SalesDynamicsFilter filter) throws IOException {
        filter.setCompanyId(getCurrentCompanyId());
        analyticsService.reportServiceCheck(filter);
        Branch branch = branchRepository.findById(filter.getBranchId() != null ? filter.getBranchId() : getCurrentBranchId())
                .orElseThrow(() -> new EntityNotFound(localizationService.getMessage("branch")));
        ProductBranch productBranch = productBranchRepository.getReferenceById(new ProductBranchId(filter.getProductId(), branch.getId(), branch.getCityKeyId()));

        try (XSSFWorkbook wb = new XSSFWorkbook(); var bos = new ByteArrayOutputStream()) {
            XSSFSheet sheet = wb.createSheet("Отчет");

            XSSFRow row = sheet.createRow(0);
            XSSFCell cell = row.createCell(0);
            cell.setCellValue("Товар " + productBranch.getName());
            cell.setCellStyle(getHeaderStyle(wb));

            CreationHelper createHelper = wb.getCreationHelper();

            CellStyle cellStyleDate = wb.createCellStyle();
            cellStyleDate.setDataFormat(createHelper.createDataFormat().getFormat("d/m/yy h:mm"));

            createHeader(wb, sheet, HEADER_TITLES, 4);
            createBody(filter, sheet, cellStyleDate);
            setColumnWidth(sheet);

            wb.write(bos);
            return new ByteArrayResource(bos.toByteArray());
        }

    }

    protected void createBody(SalesDynamicsFilter filter, XSSFSheet sheet, CellStyle style) {
        Page<SalesStatsDTO> page = clickhouseReceiptDetailRepository.getProductSalesDynamics(filter);
        int r = 5;

        XSSFWorkbook workbook = sheet.getWorkbook();
        CellStyle decimalCellStyle = workbook.createCellStyle();
        decimalCellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.000"));
        for (SalesStatsDTO salesStats : page.getContent()) {
            XSSFRow row = sheet.createRow(r++);
            int c = 0;
            XSSFCell cell = row.createCell(c++);
            cell.setCellStyle(style);
            cell.setCellValue(salesStats.getDateTime());

            cell = row.createCell(c++);
            cell.setCellStyle(decimalCellStyle);
            cell.setCellValue(salesStats.getSalesCount().doubleValue());

            cell = row.createCell(c++);
            cell.setCellStyle(decimalCellStyle);
            cell.setCellValue(salesStats.getSalesTotal().doubleValue());

            cell = row.createCell(c++);
            cell.setCellStyle(decimalCellStyle);
            cell.setCellValue(salesStats.getPrice() != null ? salesStats.getPrice().doubleValue() : 0);

            cell = row.createCell(c++);
            cell.setCellValue(salesStats.getReceiptCount().intValue());

            cell = row.createCell(c++);
            cell.setCellStyle(decimalCellStyle);
            cell.setCellValue(salesStats.getNds() != null ? salesStats.getNds().doubleValue() : 0);

            cell = row.createCell(c);
            cell.setCellStyle(decimalCellStyle);
            cell.setCellValue(salesStats.getDiscount() != null ? salesStats.getDiscount().doubleValue() : 0);
        }
        createSummaryRow(sheet, r);
    }

    private void createSummaryRow(XSSFSheet sheet, int r) {
        XSSFRow row = sheet.createRow(r);
        XSSFCell cell = row.createCell(0);
        cell.setCellValue("Итог");
        cell.setCellStyle(getSummaryStyle(sheet.getWorkbook()));

        String formula = "SUM(B5:B" + r + ")";
        cell = row.createCell(1, CellType.FORMULA);
        cell.setCellFormula(formula);
        cell.setCellStyle(getSummaryStyle(sheet.getWorkbook()));

        formula = "SUM(C5:C" + r + ")";
        cell = row.createCell(2, CellType.FORMULA);
        cell.setCellFormula(formula);
        cell.setCellStyle(getSummaryStyle(sheet.getWorkbook()));

        formula = "SUM(E5:E" + r + ")";
        cell = row.createCell(4, CellType.FORMULA);
        cell.setCellFormula(formula);
        cell.setCellStyle(getSummaryStyle(sheet.getWorkbook()));

        formula = "SUM(F5:F" + r + ")";
        cell = row.createCell(5, CellType.FORMULA);
        cell.setCellFormula(formula);
        cell.setCellStyle(getSummaryStyle(sheet.getWorkbook()));

        formula = "SUM(G5:G" + r + ")";
        cell = row.createCell(6, CellType.FORMULA);
        cell.setCellFormula(formula);
        cell.setCellStyle(getSummaryStyle(sheet.getWorkbook()));
    }

    private void setColumnWidth(XSSFSheet sheet) {
        sheet.setColumnWidth(0, 4500);//"дата"
        sheet.setColumnWidth(1, 3000);//"Кол-во"
        sheet.setColumnWidth(2, 6000);//Продано на сумму
        sheet.setColumnWidth(3, 4500);//Стоимость
        sheet.setColumnWidth(4, 4500);//Кол-во чеков
        sheet.setColumnWidth(5, 3000);//НДС
        sheet.setColumnWidth(6, 4000);//Скидка
    }
}
