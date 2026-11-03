package uz.uzkassa.smartpos.report;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.domain.postgres.catalog.CategoryBranch;
import uz.uzkassa.smartpos.domain.postgres.common.Unit;
import uz.uzkassa.smartpos.domain.postgres.common.Vat;
import uz.uzkassa.smartpos.enums.ProductFileHeader;
import uz.uzkassa.smartpos.repository.postgres.catalog.CategoryBranchRepository;
import uz.uzkassa.smartpos.repository.postgres.common.UnitRepository;
import uz.uzkassa.smartpos.repository.postgres.common.VatRepository;
import uz.uzkassa.smartpos.security.SecurityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;



@Lazy
@Component
@RequiredArgsConstructor
public class ProductImportTemplateXls extends AbstractExcelReport {
    private final UnitRepository unitRepository;
    private final VatRepository vatRepository;
    private final CategoryBranchRepository categoryBranchRepository;
    public static final EnumSet<ProductFileHeader> HEADER_TITLES = EnumSet.complementOf(EnumSet.of(ProductFileHeader.BALANCE));
    public static final EnumSet<ProductFileHeader> CATEGORY_HEADER_TITLES = EnumSet.complementOf(EnumSet.of(ProductFileHeader.CATEGORY, ProductFileHeader.BALANCE));

    public ByteArrayResource getBaos(Long branchId, Long categoryId) throws IOException {
        final boolean hasCategory = categoryId == null;
        try (XSSFWorkbook xssfWorkbook = new XSSFWorkbook(); var bos = new ByteArrayOutputStream()) {
            XSSFSheet sheet = xssfWorkbook.createSheet("Продукты");
            if (hasCategory) {
                createHeader(xssfWorkbook, HEADER_TITLES);
            } else {
                createHeader(xssfWorkbook, CATEGORY_HEADER_TITLES);
            }
            sheet.createFreezePane(0, 1);
            setColumnWidth(sheet, hasCategory);

            if (hasCategory) {
                //categories
                List<CategoryBranch> categories = categoryBranchRepository.findBranchCategories(branchId != null ? branchId : SecurityUtils.getCurrentBranchId());
                insertCategories(xssfWorkbook, categories);
            }
            //units
            List<Unit> units = unitRepository.findAll();
            insertUnits(xssfWorkbook, units, hasCategory);

            //vat
            List<Vat> vats = vatRepository.findAll();
            insertVat(xssfWorkbook, vats, hasCategory);

            //favorite
            insertFavorite(xssfWorkbook, hasCategory);

            //mark
            insertMark(xssfWorkbook, hasCategory);

            xssfWorkbook.write(bos);
            return new ByteArrayResource(bos.toByteArray());
        }

    }


    private void setColumnWidth(XSSFSheet sheet, boolean hasCategory) {
        sheet.setColumnWidth(0, 8000);//Категория
        sheet.setColumnWidth(1, 6000);//Название товара
        sheet.setColumnWidth(2, hasCategory ? 6000 : 4000);//Штрих код
        sheet.setColumnWidth(3, hasCategory ? 4000 : 6000);//Ед. Измерения
        sheet.setColumnWidth(4, hasCategory ? 6000 : 4000);//Цена
        sheet.setColumnWidth(5, 4000);//НДС %
        sheet.setColumnWidth(6, 4000);//Избранное
        sheet.setColumnWidth(7, hasCategory ? 5000 : 6000);//Маркировка
        if (hasCategory) {
            sheet.setColumnWidth(8, 6000);//ИКПУ
        }
    }

    private void insertCategories(XSSFWorkbook workbook, List<CategoryBranch> categories) {
        //hidden sheet for list values
        XSSFSheet sheet = workbook.createSheet("Справочник");

        if (CollectionUtils.isEmpty(categories)) {
            return;
        }

        XSSFRow row;
        XSSFCell cell;
        XSSFName namedRange;
        String reference;
        String prefilledValue = "";

        int r = 0;
        //put the data in
        for (CategoryBranch key : categories) {
            row = sheet.getRow(r);
            if (row == null)
                row = sheet.createRow(r);
            row.createCell(0).setCellValue(key.getName());
            if (r == 0) {
                prefilledValue = key.getName();
            }
            r++;
        }

        //create name for Category list constraint
        namedRange = workbook.createName();
        namedRange.setNameName("Categories");

        String colLetter = CellReference.convertNumToColString(0);

        reference = "Справочник!$" + colLetter + "$1:$" + colLetter + "$" + r;
        namedRange.setRefersToFormula(reference);

        //unselect that sheet because we will hide it later
        sheet.setSelected(false);


        //visible data sheet
        sheet = workbook.getSheet("Продукты");
        row = sheet.getRow(1);
        if (row == null)
            row = sheet.createRow(1);
        cell = row.getCell(0);
        if (cell == null)
            cell = row.createCell(0);
        cell.setCellValue(prefilledValue);

        //data validations
        DataValidationHelper dvHelper = sheet.getDataValidationHelper();
        //data validation for categories in A1:
        DataValidationConstraint dvConstraint = dvHelper.createFormulaListConstraint("Categories");
        CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, 0, 0);
        DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
        sheet.addValidationData(validation);

        //set Sheet1 active
        workbook.setActiveSheet(0);
    }

    private void insertUnits(XSSFWorkbook workbook, List<Unit> units, boolean hasCategory) {
        //hidden sheet for list values
        XSSFSheet sheet = hasCategory ? workbook.getSheet("Справочник") : workbook.createSheet("Справочник");

        XSSFRow row;
        XSSFCell cell;
        XSSFName namedRange;
        String reference;
        String prefilledValue = "";

        int r = 0;
        //put the data in
        for (Unit key : units) {
            row = sheet.getRow(r);
            if (row == null)
                row = sheet.createRow(r);
            row.createCell(hasCategory ? 1 : 0).setCellValue(key.getDescription());
            if (r == 0) {
                prefilledValue = key.getDescription();
            }
            r++;
        }

        //create name for Unit list constraint
        namedRange = workbook.createName();
        namedRange.setNameName("Units");

        String colLetter = CellReference.convertNumToColString(hasCategory ? 1 : 0);

        reference = "Справочник!$" + colLetter + "$1:$" + colLetter + "$" + r;
        namedRange.setRefersToFormula(reference);

        //unselect that sheet because we will hide it later
        sheet.setSelected(false);

        //visible data sheet
        sheet = workbook.getSheet("Продукты");
        row = sheet.getRow(1);
        if (row == null)
            row = sheet.createRow(1);
        cell = row.getCell(hasCategory ? 3 : 2);
        if (cell == null)
            cell = row.createCell(hasCategory ? 3 : 2);
        cell.setCellValue(prefilledValue);

        //data validations
        DataValidationHelper dvHelper = sheet.getDataValidationHelper();
        //data validation for units in D4:
        DataValidationConstraint dvConstraint = dvHelper.createFormulaListConstraint("Units");
        CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, hasCategory ? 3 : 2, hasCategory ? 3 : 2);
        DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
        sheet.addValidationData(validation);

        //set Sheet1 active
        workbook.setActiveSheet(0);
    }

    private void insertVat(XSSFWorkbook workbook, List<Vat> vats, boolean hasCategory) {
        //hidden sheet for list values
        XSSFSheet sheet = workbook.getSheet("Справочник");

        XSSFRow row = sheet.getRow(0);
        if (row == null)
            row = sheet.createRow(0);
        row.createCell(hasCategory ? 2 : 1).setCellValue("Без НДС");

        XSSFCell cell;
        XSSFName namedRange;
        String reference;
        String prefilledValue = "Без НДС";

        int r = 1;
        //put the data in
        for (Vat key : vats) {
            row = sheet.getRow(r);
            if (row == null)
                row = sheet.createRow(r);
            row.createCell(hasCategory ? 2 : 1).setCellValue(key.getPercent().toString());
            r++;
        }

        //create name for Unit list constraint
        namedRange = workbook.createName();
        namedRange.setNameName("Vats");

        String colLetter = CellReference.convertNumToColString(hasCategory ? 2 : 1);

        reference = "Справочник!$" + colLetter + "$1:$" + colLetter + "$" + r;
        namedRange.setRefersToFormula(reference);

        //unselect that sheet because we will hide it later
        sheet.setSelected(false);

        //visible data sheet
        sheet = workbook.getSheet("Продукты");
        row = sheet.getRow(1);
        if (row == null)
            row = sheet.createRow(1);
        cell = row.getCell(hasCategory ? 5 : 4);
        if (cell == null)
            cell = row.createCell(hasCategory ? 5 : 4);
        cell.setCellValue(prefilledValue);
        sheet.setActiveCell(new CellAddress(hasCategory ? "B2" : "A2"));

        //data validations
        DataValidationHelper dvHelper = sheet.getDataValidationHelper();
        //data validation for units in D4:
        DataValidationConstraint dvConstraint = dvHelper.createFormulaListConstraint("Vats");
        CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, hasCategory ? 5 : 4, hasCategory ? 5 : 4);
        DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
        sheet.addValidationData(validation);

        //set Sheet1 active
        workbook.setActiveSheet(0);
    }

    private void insertFavorite(XSSFWorkbook workbook, boolean hasCategory) {
        //hidden sheet for list values
        XSSFSheet sheet = workbook.getSheet("Справочник");

        XSSFRow row = sheet.getRow(0);
        if (row == null)
            row = sheet.createRow(0);
        row.createCell(hasCategory ? 3 : 2).setCellValue("Да");

        row = sheet.getRow(1);
        if (row == null)
            row = sheet.createRow(1);
        row.createCell(hasCategory ? 3 : 2).setCellValue("Нет");

        //create name for Unit list constraint
        XSSFName namedRange = workbook.createName();
        namedRange.setNameName("Favourites");

        String colLetter = CellReference.convertNumToColString(hasCategory ? 3 : 2);
        String reference = "Справочник!$" + colLetter + "$1:$" + colLetter + "$" + 2;
        namedRange.setRefersToFormula(reference);

        //unselect that sheet because we will hide it later
        sheet.setSelected(false);

        //visible data sheet
        sheet = workbook.getSheet("Продукты");
        row = sheet.getRow(1);
        if (row == null)
            row = sheet.createRow(1);
        XSSFCell cell = row.getCell(hasCategory ? 6 : 5);
        if (cell == null)
            cell = row.createCell(hasCategory ? 6 : 5);
        cell.setCellValue("Да");
        sheet.setActiveCell(new CellAddress(hasCategory ? "B2" : "A2"));

        //data validations
        DataValidationHelper dvHelper = sheet.getDataValidationHelper();
        //data validation for units in D4:
        DataValidationConstraint dvConstraint = dvHelper.createFormulaListConstraint("Favourites");
        CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, hasCategory ? 6 : 5, hasCategory ? 6 : 5);
        DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
        sheet.addValidationData(validation);

        //set Sheet1 active
        workbook.setActiveSheet(0);
    }

    void createHeader(XSSFWorkbook wb, EnumSet<ProductFileHeader> headerTitles) {
        CellStyle headerCellStyle = getHeaderStyle(wb);
        XSSFSheet sheet = wb.getSheetAt(0);
        XSSFRow headerRow = sheet.createRow(0);

        int i = 0;
        for (ProductFileHeader header : headerTitles) {
            XSSFCell cell = headerRow.createCell(i++);
            cell.setCellValue(header.getNameRu());
            cell.setCellStyle(headerCellStyle);
        }
    }

    private void insertMark(XSSFWorkbook workbook, boolean hasCategory) {
        //visible data sheet
        XSSFSheet sheet = workbook.getSheet("Продукты");
        XSSFRow row = sheet.getRow(1);
        if (row == null) row = sheet.createRow(1);
        XSSFCell cell = row.getCell(hasCategory ? 7 : 6);
        if (cell == null) cell = row.createCell(hasCategory ? 7 : 6);
        cell.setCellValue("Да");
        sheet.setActiveCell(new CellAddress(hasCategory ? "B2" : "A2"));

        //data validations
        DataValidationHelper dvHelper = sheet.getDataValidationHelper();
        //data validation for units in D4:
        DataValidationConstraint dvConstraint = dvHelper.createFormulaListConstraint("Favourites");
        CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, hasCategory ? 7 : 6, hasCategory ? 7 : 6);
        DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
        sheet.addValidationData(validation);

        //set Sheet1 active
        workbook.setActiveSheet(0);
    }
}
