package uz.uzkassa.cts.dto.fm;

import lombok.*;
import uz.uzkassa.cts.helper.excelhelper.annotaion.XlsxSheet;
import uz.uzkassa.cts.helper.excelhelper.annotaion.XlsxSingleField;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XlsxSheet(value = "FMReport")
@Builder
public class FMReportExcelResponseDTO {
    @XlsxSingleField(columnIndex = 0, columnName = "ИНН")
    private String tin;
    @XlsxSingleField(columnIndex = 1, columnName = "Клиент")
    private String customerName;
    @XlsxSingleField(columnIndex = 2, columnName = "ККМ")
    private String deviceSerialNumber;
    @XlsxSingleField(columnIndex = 3, columnName = "ФМ")
    private String fmSerialNumber;
    @XlsxSingleField(columnIndex = 4, columnName = "Статус ФМ причин")
    private String caseType;
    @XlsxSingleField(columnIndex = 5, columnName = "Статус Прихода ФМ")
    private String arrivalStatus;
    @XlsxSingleField(columnIndex = 6, columnName = "Дата прихода ФМ")
    private String arrivalDate;
    @XlsxSingleField(columnIndex = 7, columnName = "Оператор")
    private String caseCreatorName;
    @XlsxSingleField(columnIndex = 8, columnName = "Регион")
    private String address;
    @XlsxSingleField(columnIndex = 9, columnName = "Кейс ID")
    private String caseId;
    @XlsxSingleField(columnIndex = 10, columnName = "Дата принятие кейса")
    private String createdDate;
}
