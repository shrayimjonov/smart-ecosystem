package uz.uzkassa.cts.dto.humoconnect.response;

import lombok.*;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.enums.DocFlowStatus;
import uz.uzkassa.cts.enums.HumoConnectOperatorStatus;
import uz.uzkassa.cts.enums.HumoConnectStatus;
import uz.uzkassa.cts.helper.excelhelper.annotaion.XlsxSheet;
import uz.uzkassa.cts.helper.excelhelper.annotaion.XlsxSingleField;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XlsxSheet(value = "HumoConnect")
@Builder
public class HumoConnectExcelResponseDto {
    @XlsxSingleField(columnIndex = 0, columnName = "Дата создания")
    private String createdDate;
    @XlsxSingleField(columnIndex = 1, columnName = "Создатель филиала")
    private String creatorBranch;
    @XlsxSingleField(columnIndex = 2, columnName = "Форма собственности")
    private String businessType;
    @XlsxSingleField(columnIndex = 3, columnName = "Клиент")
    private String customerName;
    @XlsxSingleField(columnIndex = 4, columnName = "Адрес")
    private String address;
    @XlsxSingleField(columnIndex = 5, columnName = "Сфера деятельности")
    private String activityType;
    @XlsxSingleField(columnIndex = 6, columnName = "Телефон")
    private String phone;
    @XlsxSingleField(columnIndex = 7, columnName = "МФО")
    private String mfo;
    @XlsxSingleField(columnIndex = 8, columnName = "Номер счета")
    private String accountNumber;
    @XlsxSingleField(columnIndex = 9, columnName = "ИНН")
    private String customerTin;
    @XlsxSingleField(columnIndex = 10, columnName = "Серийный номер устройства")
    private String deviceSerialNumber;
    @XlsxSingleField(columnIndex = 11, columnName = "Статус")
    private String status;
    @XlsxSingleField(columnIndex = 12, columnName = "Статус оператора")
    private String operatorStatus;
    @XlsxSingleField(columnIndex = 13, columnName = "Дата последнего изменения")
    private String lastModifiedDate;
    @XlsxSingleField(columnIndex = 14, columnName = "Последний модификатор")
    private String lastModifier;
    @XlsxSingleField(columnIndex = 15, columnName = "Имя создателя Кейс")
    private String caseCreatorName;
    @XlsxSingleField(columnIndex = 16, columnName = "Лицензия")
    private String license;
    @XlsxSingleField(columnIndex = 17, columnName = "Файл")
    private String fileUrl;
    @XlsxSingleField(columnIndex = 18, columnName = "Кейс ИД")
    private Long caseId;
    @XlsxSingleField(columnIndex = 19, columnName = "Статус документа")
    private String docFlowStatus;

    public String getStatus() {
        return StringUtils.isNotEmpty(status) ? HumoConnectStatus.fromOrdinal(Integer.parseInt(status)).getName() : "";
    }

    public String getOperatorStatus() {
        return StringUtils.isNotEmpty(operatorStatus) ? HumoConnectOperatorStatus.fromOrdinal(Integer.parseInt(operatorStatus)).getName() : "";
    }

    public String getDocFlowStatus() {
        return StringUtils.isNotEmpty(docFlowStatus) ? DocFlowStatus.get(docFlowStatus).getName() : "";
    }
}
