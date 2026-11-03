package uz.uzkassa.cts.dto.cases;

import lombok.*;
import uz.uzkassa.cts.helper.excelhelper.annotaion.XlsxSheet;
import uz.uzkassa.cts.helper.excelhelper.annotaion.XlsxSingleField;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XlsxSheet(value = "Case")
public class CaseExcelExportDto {
    @XlsxSingleField(columnIndex = 0, columnName = "ИНН")
    String customerTin;
    @XlsxSingleField(columnIndex = 1, columnName = "КЛИЕНТ")
    String customerName;
    @XlsxSingleField(columnIndex = 2, columnName = "ТИП ИЗМИНЕНИЯ")
    String caseType;
    @XlsxSingleField(columnIndex = 3, columnName = "ККМ Сер.н")
    String deviceSerial;
    @XlsxSingleField(columnIndex = 4, columnName = "ККМ Новый Сер.н")
    String newDeviceSerial;
    @XlsxSingleField(columnIndex = 5, columnName = "ФМ Сер.н")
    String fmSerial;
    @XlsxSingleField(columnIndex = 6, columnName = "ФМ Новый Сер.н")
    String newFmSerial;
    @XlsxSingleField(columnIndex = 7, columnName = "ПРИЧИНА")
    String jsonProperties;
    @XlsxSingleField(columnIndex = 8, columnName = "ОПЕРАТОР")
    String operatorName;
    @XlsxSingleField(columnIndex = 9, columnName = "РЕГИОН")
    String operatorBranch;
    @XlsxSingleField(columnIndex = 10, columnName = "СТАТУС")
    String status;
    @XlsxSingleField(columnIndex = 11, columnName = "ДАТА СОЗДАНИЕ")
    String createDate;
    @XlsxSingleField(columnIndex = 12, columnName = "ДАТА ПОСЛЕДНЕГО ИЗМЕНЕНИЯ")
    String lastModifiedDate;
    @XlsxSingleField(columnIndex = 13, columnName = "КЕЙС ID")
    Long id;
    @XlsxSingleField(columnIndex = 14, columnName = "Старый менеджер")
    String oldManager;
    @XlsxSingleField(columnIndex = 15, columnName = "Новый менеджер")
    String newManager;
}
