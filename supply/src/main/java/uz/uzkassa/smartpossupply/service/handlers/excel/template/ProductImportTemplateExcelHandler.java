package uz.uzkassa.smartpossupply.service.handlers.excel.template;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFName;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.ProductOrigin;
import uz.uzkassa.smartpossupply.enums.Vat;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 05.04.2023
 */
@Component
public class ProductImportTemplateExcelHandler extends AbstractImportTemplateExcelHandler implements ITemplateExcelHandler {

    @Override
    public List<String> getHeaders() {
        return List.of(
                "ИКПУ",
                "Название товара",
                "Штрихкод",
                "Маркировка",
                "Происхождение продукта",
                "Цена продажи",
                "НДС",
                "Вес брутто (в тоннах)",
                "Вес нетто (в тоннах)"
        );
    }

    @Override
    public String getFileName() {
        return "Products";
    }

    @Override
    public XSSFWorkbook getWorkbook() {
        XSSFWorkbook xssfWorkbook = getXSSFWorkbook(getFileName(), getHeaders());
        insertVatOptionList(xssfWorkbook);
        insertMarkOptionList(xssfWorkbook);
        insertProductOriginOptionList(xssfWorkbook);
        return xssfWorkbook;
    }

    @Override
    public ResponseEntity<Resource> download() {
        return getTemplate(getWorkbook(), getFileName());
    }

    private void insertVatOptionList(XSSFWorkbook workbook) {
        List<Vat> vatList = Stream.of(Vat.WITHOUT_VAT, Vat.VAT_0, Vat.VAT_6, Vat.VAT_12).toList();
        XSSFSheet sheet = workbook.createSheet("VatList");
        if (CollectionUtils.isEmpty(vatList)) {
            return;
        }
        XSSFRow row;
        XSSFCell cell;
        XSSFName namedRange;
        String reference;
        String prefilledValue = "";
        int r = 0;
        for (Vat vat : vatList) {
            row = sheet.getRow(r);
            if (row == null)
                row = sheet.createRow(r);
            row.createCell(0).setCellValue(vat.getName().concat(" -").concat(vat.getCode()));
            if (r == 0) {
                prefilledValue = vat.getName().concat(" -").concat(vat.getCode());
            }
            r++;
        }
        namedRange = workbook.createName();
        namedRange.setNameName("VatList");

        String colLetter = CellReference.convertNumToColString(0);
        reference = "VatList!$" + colLetter + "$1:$" + colLetter + "$" + r;
        namedRange.setRefersToFormula(reference);

        sheet.setSelected(false);

        sheet = workbook.getSheet(getFileName());
        row = sheet.getRow(1);
        if (row == null)
            row = sheet.createRow(1);
        cell = row.getCell(6);
        if (cell == null)
            cell = row.createCell(6);
        cell.setCellValue(prefilledValue);

        DataValidationHelper dvHelper = sheet.getDataValidationHelper();
        DataValidationConstraint dvConstraint = dvHelper.createFormulaListConstraint("VatList");
        CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, 6, 6);
        DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
        sheet.addValidationData(validation);

        workbook.setActiveSheet(0);
    }

    private void insertProductOriginOptionList(XSSFWorkbook workbook) {
        List<ProductOrigin> optionList = Arrays.stream(ProductOrigin.values()).toList();
        XSSFSheet sheet = workbook.createSheet("ProductOriginList");
        if (CollectionUtils.isEmpty(optionList)) {
            return;
        }
        XSSFRow row;
        XSSFCell cell;
        XSSFName namedRange;
        String reference;
        String prefilledValue = "";
        int r = 0;
        for (ProductOrigin option : optionList) {
            row = sheet.getRow(r);
            if (row == null)
                row = sheet.createRow(r);
            row.createCell(0).setCellValue(option.getName().concat(" -").concat(option.getCode()));
            if (r == 0) {
                prefilledValue = option.getName().concat(" -").concat(option.getCode());
            }
            r++;
        }
        namedRange = workbook.createName();
        namedRange.setNameName("ProductOriginList");

        String colLetter = CellReference.convertNumToColString(0);
        reference = "ProductOriginList!$" + colLetter + "$1:$" + colLetter + "$" + r;
        namedRange.setRefersToFormula(reference);

        sheet.setSelected(false);

        sheet = workbook.getSheet(getFileName());
        row = sheet.getRow(1);
        if (row == null)
            row = sheet.createRow(1);
        cell = row.getCell(4);
        if (cell == null)
            cell = row.createCell(4);
        cell.setCellValue(prefilledValue);

        DataValidationHelper dvHelper = sheet.getDataValidationHelper();
        DataValidationConstraint dvConstraint = dvHelper.createFormulaListConstraint("ProductOriginList");
        CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, 4, 4);
        DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
        sheet.addValidationData(validation);

        workbook.setActiveSheet(0);
    }

    private void insertMarkOptionList(XSSFWorkbook workbook) {
        Map<String, Boolean> optionList = new LinkedHashMap<>();
        optionList.put("Yes", Boolean.TRUE);
        optionList.put("No", Boolean.FALSE);
        XSSFSheet sheet = workbook.createSheet("MarkOptionList");
        XSSFRow row;
        XSSFCell cell;
        XSSFName namedRange;
        String reference;
        String prefilledValue = "";
        int r = 0;
        for (Map.Entry<String, Boolean> entry : optionList.entrySet()) {
            row = sheet.getRow(r);
            if (row == null)
                row = sheet.createRow(r);
            row.createCell(0).setCellValue(entry.getKey().concat(" -").concat(entry.getValue().toString()));
            if (r == 0) {
                prefilledValue = entry.getKey().concat(" -").concat(entry.getValue().toString());
            }
            r++;
        }
        namedRange = workbook.createName();
        namedRange.setNameName("MarkOptionList");

        String colLetter = CellReference.convertNumToColString(0);
        reference = "MarkOptionList!$" + colLetter + "$1:$" + colLetter + "$" + r;
        namedRange.setRefersToFormula(reference);

        sheet.setSelected(false);

        sheet = workbook.getSheet(getFileName());
        row = sheet.getRow(1);
        if (row == null)
            row = sheet.createRow(1);
        cell = row.getCell(3);
        if (cell == null)
            cell = row.createCell(3);
        cell.setCellValue(prefilledValue);

        DataValidationHelper dvHelper = sheet.getDataValidationHelper();
        DataValidationConstraint dvConstraint = dvHelper.createFormulaListConstraint("MarkOptionList");
        CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, 3, 3);
        DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
        sheet.addValidationData(validation);

        workbook.setActiveSheet(0);
    }
}
