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
import uz.uzkassa.cts.enums.ArrivalStatus;
import uz.uzkassa.cts.enums.FMReportReason;
import uz.uzkassa.cts.enums.SoliqTransferStatus;

import java.util.List;


@Component
public class FiscalModuleReportTemplate extends AbstractImportExcelTemplateGenerator implements ImportExcelTemplateGenerator {

    private static final String ARRIVAL_STATUSES = "ArrivalStatuses";
    private static final String REASONS = "Reasons";
    private static final String TRANSFER_STATUSES = "TransferStatuses";

    @Override
    public String getFileName() {
        return "FiscalModuleReport";
    }

    @Override
    public List<String> getHeaders() {
        return List.of(
                "ИНН",
                "ККМ",
                "Номер Фискального модуля",
                "Статус принятие кейса",
                "Статус прихода ФМ",
                "Статус переданного в НИЦ"
        );
    }

    @Override
    public XSSFWorkbook getWorkbook() {
        XSSFWorkbook xssfWorkbook = getXSSFWorkbook(getFileName(), getHeaders());
        insertArrivalStatusList(xssfWorkbook);
        insertFMReportReasonList(xssfWorkbook);
        insertSoliqTransferStatusList(xssfWorkbook);
        return xssfWorkbook;
    }

    @Override
    public ResponseEntity<Resource> download() {
        return generateTemplate(getWorkbook(), getFileName());
    }

    private void insertArrivalStatusList(XSSFWorkbook xssfWorkbook) {
        XSSFSheet sheet = xssfWorkbook.createSheet(ARRIVAL_STATUSES);
        XSSFRow row;
        XSSFCell cell;
        XSSFName namedRange;
        String reference;
        String prefilledValue = "";
        int r = 0;

        for (ArrivalStatus arrivalStatus : ArrivalStatus.values()) {
            row = sheet.createRow(r);
            String value = arrivalStatus.getName() + " - " + arrivalStatus.getCode();
            row.createCell(0).setCellValue(value);
            if (r == 0) {
                prefilledValue = value;
            }
            r++;
        }

        namedRange = xssfWorkbook.createName();
        namedRange.setNameName(ARRIVAL_STATUSES);
        String colLetter = CellReference.convertNumToColString(0);
        reference = ARRIVAL_STATUSES + "!$" + colLetter + "$1:$" + colLetter + "$" + r;
        namedRange.setRefersToFormula(reference);

        sheet.setSelected(false);

        sheet = xssfWorkbook.getSheet(getFileName());
        row = sheet.getRow(1);
        if (row == null)
            row = sheet.createRow(1);
        cell = row.getCell(4);
        if (cell == null)
            cell = row.createCell(4);
        cell.setCellValue(prefilledValue);

        DataValidationHelper dvHelper = sheet.getDataValidationHelper();
        DataValidationConstraint dvConstraint = dvHelper.createFormulaListConstraint(ARRIVAL_STATUSES);
        CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, 4, 4);
        DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
        sheet.addValidationData(validation);
    }

    private void insertFMReportReasonList(XSSFWorkbook xssfWorkbook) {
        XSSFSheet sheet = xssfWorkbook.createSheet(REASONS);
        XSSFRow row;
        XSSFCell cell;
        XSSFName namedRange;
        String reference;
        String prefilledValue = "";
        int r = 0;

        for (FMReportReason reason : FMReportReason.values()) {
            row = sheet.createRow(r);
            String value = reason.getName() + " - " + reason.getCode();
            row.createCell(0).setCellValue(value);
            if (r == 0) {
                prefilledValue = value;
            }
            r++;
        }

        namedRange = xssfWorkbook.createName();
        namedRange.setNameName(REASONS);
        String colLetter = CellReference.convertNumToColString(0);
        reference = REASONS + "!$" + colLetter + "$1:$" + colLetter + "$" + r;
        namedRange.setRefersToFormula(reference);

        sheet.setSelected(false);

        sheet = xssfWorkbook.getSheet(getFileName());
        row = sheet.getRow(1);
        if (row == null)
            row = sheet.createRow(1);
        cell = row.getCell(3);
        if (cell == null)
            cell = row.createCell(3);
        cell.setCellValue(prefilledValue);

        DataValidationHelper dvHelper = sheet.getDataValidationHelper();
        DataValidationConstraint dvConstraint = dvHelper.createFormulaListConstraint(REASONS);
        CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, 3, 3);
        DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
        sheet.addValidationData(validation);
    }

    private void insertSoliqTransferStatusList(XSSFWorkbook xssfWorkbook) {
        XSSFSheet sheet = xssfWorkbook.createSheet(TRANSFER_STATUSES);
        XSSFRow row;
        XSSFCell cell;
        XSSFName namedRange;
        String reference;
        String prefilledValue = "";
        int r = 0;

        for (SoliqTransferStatus status : SoliqTransferStatus.values()) {
            row = sheet.createRow(r);
            String value = status.getName() + " - " + status.getCode();
            row.createCell(0).setCellValue(value);
            if (r == 0) {
                prefilledValue = value;
            }
            r++;
        }

        namedRange = xssfWorkbook.createName();
        namedRange.setNameName(TRANSFER_STATUSES);
        String colLetter = CellReference.convertNumToColString(0);
        reference = TRANSFER_STATUSES + "!$" + colLetter + "$1:$" + colLetter + "$" + r;
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
        DataValidationConstraint dvConstraint = dvHelper.createFormulaListConstraint(TRANSFER_STATUSES);
        CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, 5, 5);
        DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
        sheet.addValidationData(validation);

        xssfWorkbook.setActiveSheet(0);
    }
}
