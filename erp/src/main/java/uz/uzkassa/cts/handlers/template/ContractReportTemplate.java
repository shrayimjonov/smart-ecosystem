package uz.uzkassa.cts.handlers.template;

import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.enums.ActDocFlowStatus;
import uz.uzkassa.cts.enums.ContractReportInvoiceSaleStatus;
import uz.uzkassa.cts.enums.ContractReportInvoiceStatus;
import uz.uzkassa.cts.enums.ContractReportPaymentType;

import java.util.List;

/**
 * Created date time: 18.03.2025 14:25
 */

@Component
public class ContractReportTemplate extends AbstractImportExcelTemplateGenerator implements ImportExcelTemplateGenerator {

    private static final String INVOICE_STATUSES = "InvoiceStatuses";
    private static final String REALISATIONS = "Realisations";
    private static final String DOCUMENT_STATUSES = "DocumentStatuses";
    private static final String SALE_STATUSES = "SaleStatuses";

    @Override
    public String getFileName() {
        return "ContractReport";
    }

    @Override
    public List<String> getHeaders() {
        return List.of("Серийный номер", "ИНН", "Название", "Договор", "Сумма", "Статус счет-фактуры", "Реализация", "Регион", "Комментарий", "Статус документа", "Статус покупки", "Замена", "Причина", "Дата");
    }

    @Override
    public XSSFWorkbook getWorkbook() {
        XSSFWorkbook xssfWorkbook = getXSSFWorkbook(getFileName(), getHeaders());
        insertInvoiceStatusList(xssfWorkbook);
        insertRealisationList(xssfWorkbook);
        insertDocumentStatusList(xssfWorkbook);
        insertSaleStatusList(xssfWorkbook);
        return xssfWorkbook;
    }

    @Override
    public ResponseEntity<Resource> download() {
        return generateTemplate(getWorkbook(), getFileName());
    }

    private void insertInvoiceStatusList(XSSFWorkbook xssfWorkbook) {
        XSSFSheet sheet = xssfWorkbook.createSheet(INVOICE_STATUSES);
        XSSFRow row;
        XSSFCell cell;
        XSSFName namedRange;
        String reference;
        String prefilledValue = "";
        int r = 0;

        for (ContractReportInvoiceStatus invoiceStatus : ContractReportInvoiceStatus.values()) {
            row = sheet.createRow(r);
            String value = invoiceStatus.getName() + " - " + invoiceStatus.getCode();
            row.createCell(0).setCellValue(value);
            if (r == 0) {
                prefilledValue = value;
            }
            r++;
        }

        namedRange = xssfWorkbook.createName();
        namedRange.setNameName(INVOICE_STATUSES);
        String colLetter = CellReference.convertNumToColString(0);
        reference = INVOICE_STATUSES + "!$" + colLetter + "$1:$" + colLetter + "$" + r;
        namedRange.setRefersToFormula(reference);

        sheet.setSelected(false);

        sheet = xssfWorkbook.getSheet(getFileName());
        row = sheet.getRow(1);
        if (row == null)
            row = sheet.createRow(1);
        cell = row.getCell(5);
        if (cell == null)
            cell = row.createCell(5);
        cell.setCellValue(prefilledValue);

        DataValidationHelper dvHelper = sheet.getDataValidationHelper();
        DataValidationConstraint dvConstraint = dvHelper.createFormulaListConstraint(INVOICE_STATUSES);
        CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, 5, 5);
        DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
        sheet.addValidationData(validation);
    }

    private void insertRealisationList(XSSFWorkbook xssfWorkbook) {
        XSSFSheet sheet = xssfWorkbook.createSheet(REALISATIONS);
        XSSFRow row;
        XSSFCell cell;
        XSSFName namedRange;
        String reference;
        String prefilledValue = "";
        int r = 0;

        for (ContractReportPaymentType realisation : ContractReportPaymentType.values()) {
            row = sheet.createRow(r);
            String value = realisation.getName() + " - " + realisation.getCode();
            row.createCell(0).setCellValue(value);
            if (r == 0) {
                prefilledValue = value;
            }
            r++;
        }

        namedRange = xssfWorkbook.createName();
        namedRange.setNameName(REALISATIONS);
        String colLetter = CellReference.convertNumToColString(0);
        reference = REALISATIONS + "!$" + colLetter + "$1:$" + colLetter + "$" + r;
        namedRange.setRefersToFormula(reference);

        sheet.setSelected(false);

        sheet = xssfWorkbook.getSheet(getFileName());
        row = sheet.getRow(1);
        if (row == null)
            row = sheet.createRow(1);
        cell = row.getCell(6);
        if (cell == null)
            cell = row.createCell(6);
        cell.setCellValue(prefilledValue);

        DataValidationHelper dvHelper = sheet.getDataValidationHelper();
        DataValidationConstraint dvConstraint = dvHelper.createFormulaListConstraint(REALISATIONS);
        CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, 6, 6);
        DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
        sheet.addValidationData(validation);
    }

    private void insertDocumentStatusList(XSSFWorkbook xssfWorkbook) {
        XSSFSheet sheet = xssfWorkbook.createSheet(DOCUMENT_STATUSES);
        XSSFRow row;
        XSSFCell cell;
        XSSFName namedRange;
        String reference;
        String prefilledValue = "";
        int r = 0;

        for (ActDocFlowStatus documentStatus : ActDocFlowStatus.values()) {
            row = sheet.createRow(r);
            String value = documentStatus.getName() + " - " + documentStatus.getCode();
            row.createCell(0).setCellValue(value);
            if (r == 0) {
                prefilledValue = value;
            }
            r++;
        }

        namedRange = xssfWorkbook.createName();
        namedRange.setNameName(DOCUMENT_STATUSES);
        String colLetter = CellReference.convertNumToColString(0);
        reference = DOCUMENT_STATUSES + "!$" + colLetter + "$1:$" + colLetter + "$" + r;
        namedRange.setRefersToFormula(reference);

        sheet.setSelected(false);

        sheet = xssfWorkbook.getSheet(getFileName());
        row = sheet.getRow(1);
        if (row == null)
            row = sheet.createRow(1);
        cell = row.getCell(9);
        if (cell == null)
            cell = row.createCell(9);
        cell.setCellValue(prefilledValue);

        DataValidationHelper dvHelper = sheet.getDataValidationHelper();
        DataValidationConstraint dvConstraint = dvHelper.createFormulaListConstraint(DOCUMENT_STATUSES);
        CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, 9, 9);
        DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
        sheet.addValidationData(validation);

        xssfWorkbook.setActiveSheet(0);
    }

    private void insertSaleStatusList(XSSFWorkbook xssfWorkbook) {
        XSSFSheet sheet = xssfWorkbook.createSheet(SALE_STATUSES);
        XSSFRow row;
        XSSFCell cell;
        XSSFName namedRange;
        String reference;
        String prefilledValue = "";
        int r = 0;

        for (ContractReportInvoiceSaleStatus saleStatus : ContractReportInvoiceSaleStatus.values()) {
            row = sheet.createRow(r);
            String value = saleStatus.getName() + " - " + saleStatus.getCode();
            row.createCell(0).setCellValue(value);
            if (r == 0) {
                prefilledValue = value;
            }
            r++;
        }

        namedRange = xssfWorkbook.createName();
        namedRange.setNameName(SALE_STATUSES);
        String colLetter = CellReference.convertNumToColString(0);
        reference = SALE_STATUSES + "!$" + colLetter + "$1:$" + colLetter + "$" + r;
        namedRange.setRefersToFormula(reference);

        sheet.setSelected(false);

        sheet = xssfWorkbook.getSheet(getFileName());
        row = sheet.getRow(1);
        if (row == null)
            row = sheet.createRow(1);
        cell = row.getCell(10);
        if (cell == null)
            cell = row.createCell(10);
        cell.setCellValue(prefilledValue);

        DataValidationHelper dvHelper = sheet.getDataValidationHelper();
        DataValidationConstraint dvConstraint = dvHelper.createFormulaListConstraint(SALE_STATUSES);
        CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, 10, 10);
        DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
        sheet.addValidationData(validation);

        xssfWorkbook.setActiveSheet(0);
    }
}
