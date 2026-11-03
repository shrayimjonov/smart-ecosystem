package uz.uzkassa.cts.dto.report;

import lombok.*;
import uz.uzkassa.cts.helper.excelhelper.annotaion.XlsxSheet;
import uz.uzkassa.cts.helper.excelhelper.annotaion.XlsxSingleField;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XlsxSheet(value = "Contract report")
@Builder
public class ContractReportExcelExportDto {
    @XlsxSingleField(columnIndex = 0, columnName = "Device Serial Number")
    String serialNumber;
    @XlsxSingleField(columnIndex = 1, columnName = "Customer TIN")
    String tin;
    @XlsxSingleField(columnIndex = 2, columnName = "Company")
    String name;
    @XlsxSingleField(columnIndex = 3, columnName = "Contract number")
    String number;
    @XlsxSingleField(columnIndex = 4, columnName = "Sum")
    BigDecimal amount;
    @XlsxSingleField(columnIndex = 5, columnName = "Invoice status")
    String invoiceStatus;
    @XlsxSingleField(columnIndex = 6, columnName = "Realisation")
    String realisation;
    @XlsxSingleField(columnIndex = 7, columnName = "Region")
    String region;
    @XlsxSingleField(columnIndex = 8, columnName = "Comment")
    String comment;
    @XlsxSingleField(columnIndex = 9, columnName = "Document status")
    String documentStatus;
    @XlsxSingleField(columnIndex = 10, columnName = "Sale status")
    String saleStatus;
    @XlsxSingleField(columnIndex = 11, columnName = "Device replacement")
    String deviceReplacement;
    @XlsxSingleField(columnIndex = 12, columnName = "Reason")
    String reason;
    @XlsxSingleField(columnIndex = 13, columnName = "Date")
    String date;
    @XlsxSingleField(columnIndex = 14, columnName = "Last modifier")
    String lastModifier;
    @XlsxSingleField(columnIndex = 15, columnName = "Last modified Date")
    String lastModifiedDate;
}
