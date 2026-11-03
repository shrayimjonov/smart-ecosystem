package uz.uzkassa.smartpos.dto.integration.quote;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ContractStatus {
    CREATED("CREATED", "СОЗДАНО", "ЯРАТИЛДИ", "YARATILDI"),
    DOCUMENT_ATTACHED("DOCUMENT_ATTACHED", "ДОКУМЕНТ ПРИКРЕПЛЁН", "ХУЖЖАТ ИЛОВА КИЛИНГАН", "HUJJAT ILOVA QILINGAN"),
    WAITING_FOR_DOCUMENT_ATTACHMENT("WAITING_FOR_DOCUMENT_ATTACHMENT", "ОЖИДАЕТСЯ ПРИКРЕПЛЕНИЕ ДОКУМЕНТА", "ХУЖЖАТ ИЛОВА КИЛИНИШИ КУТИЛМОКДА", "HUJJAT ILOVA QILINISHI KUTILMOQDA"),
    ACCEPTED("ACCEPTED", "ПРИНЯТ", "ТАСДИКЛАНГАН", "TASDIQLANGAN"),
    CLOSED("CLOSED", "ЗАКРЫТО", "ЕПИЛГАН", "YOPILGAN"),
    REJECTED("REJECTED", "ОТКЛОНЕН", "РАД КИЛИНГАН", "RAD QILINGAN"),
    BALANCE_NOT_ENOUGH("BALANCE_NOT_ENOUGH", "Недостаточно средств", "Маблаг етарли эмас", "Mablag' yetarli emas");

    private final String nameRu;
    private final String nameUzCyrillic;
    private final String nameUzLatin;
    private final String code;

    ContractStatus(String code, String nameRu, String nameUzCyrillic, String nameUzLatin) {
        this.code = code;
        this.nameRu = nameRu;
        this.nameUzCyrillic = nameUzCyrillic;
        this.nameUzLatin = nameUzLatin;
    }

    @JsonCreator
    public static ContractStatus forValue(@JsonProperty("code") String code) {
        return ContractStatus.valueOf(code);
    }
}
