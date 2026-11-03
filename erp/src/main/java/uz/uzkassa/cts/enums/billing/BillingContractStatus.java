package uz.uzkassa.cts.enums.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BillingContractStatus {
    @JsonProperty("CREATED")
    CREATED("CREATED", "СОЗДАНО", "ЯРАТИЛДИ", "YARATILDI"),
    @JsonProperty("DOCUMENT_ATTACHED")
    DOCUMENT_ATTACHED("DOCUMENT_ATTACHED", "ДОКУМЕНТ ПРИКРЕПЛЁН", "ХУЖЖАТ ИЛОВА КИЛИНГАН", "HUJJAT ILOVA QILINGAN"),
    @JsonProperty("WAITING_FOR_DOCUMENT_ATTACHMENT")
    WAITING_FOR_DOCUMENT_ATTACHMENT("WAITING_FOR_DOCUMENT_ATTACHMENT", "ОЖИДАЕТСЯ ПРИКРЕПЛЕНИЕ ДОКУМЕНТА", "ХУЖЖАТ ИЛОВА КИЛИНИШИ КУТИЛМОКДА", "HUJJAT ILOVA QILINISHI KUTILMOQDA"),
    @JsonProperty("ACCEPTED")
    ACCEPTED("ACCEPTED", "ПРИНЯТ", "ТАСДИКЛАНГАН", "TASDIQLANGAN"),
    @JsonProperty("CLOSED")
    CLOSED("CLOSED", "ЗАКРЫТО", "ЕПИЛГАН", "YOPILGAN"),
    @JsonProperty("REJECTED")
    REJECTED("REJECTED", "ОТКЛОНЕН", "РАД КИЛИНГАН", "RAD QILINGAN"),
    @JsonProperty("BALANCE_NOT_ENOUGH")
    BALANCE_NOT_ENOUGH("BALANCE_NOT_ENOUGH", "Недостаточно средств", "Маблаг етарли эмас", "Mablag' yetarli emas");

    private final String nameRu;
    private final String nameUzCyrillic;
    private final String nameUzLatin;
    private final String code;

    BillingContractStatus(String code, String nameRu, String nameUzCyrillic, String nameUzLatin) {
        this.code = code;
        this.nameRu = nameRu;
        this.nameUzCyrillic = nameUzCyrillic;
        this.nameUzLatin = nameUzLatin;
    }

    @JsonCreator
    static BillingContractStatus findValue(@JsonProperty("code") String code) {
        return Arrays.stream(BillingContractStatus.values()).filter(pt -> pt.code.equals(code)).findFirst().orElseThrow();
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    static BillingContractStatus getValue(@JsonProperty("code") String code) {
        return Arrays.stream(BillingContractStatus.values()).filter(pt -> pt.code.equals(code)).findFirst().orElseThrow();
    }
}
