package uz.uzkassa.cts.dto.sale.invoice;

import lombok.*;
import uz.uzkassa.cts.helper.excelhelper.annotaion.XlsxSheet;
import uz.uzkassa.cts.helper.excelhelper.annotaion.XlsxSingleField;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XlsxSheet(value = "SaleInvoice")
@Builder
public class SaleInvoiceExcelExportDto {
    @XlsxSingleField(columnIndex = 0, columnName = "ИНН")
    String customerTin;
    @XlsxSingleField(columnIndex = 1, columnName = "Название компании")
    String customerName;
    @XlsxSingleField(columnIndex = 2, columnName = "Регион")
    String region;
    @XlsxSingleField(columnIndex = 3, columnName = "Адрес")
    String address;
    @XlsxSingleField(columnIndex = 4, columnName = "Номер телефона")
    String phone;
    @XlsxSingleField(columnIndex = 5, columnName = "МФО")
    String bankCode;
    @XlsxSingleField(columnIndex = 6, columnName = "Расчётный счёт")
    String accountNumber;
    @XlsxSingleField(columnIndex = 7, columnName = "Номер с/ф")
    String invoiceNumber;
    @XlsxSingleField(columnIndex = 8, columnName = "Дата с/ф")
    String invoiceCreatedDate;
    @XlsxSingleField(columnIndex = 9, columnName = "Сумма")
    Double totalSellPrice;
    @XlsxSingleField(columnIndex = 10, columnName = "Статус")
    String invoiceStatus;
}
