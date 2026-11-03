package uz.uzkassa.smartpos.integration.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BillingPromotionStatus {
    CREATED("CREATED", "СОЗДАНО", "ЯРАТИЛДИ", "YARATILDI"),
    ACTIVE( "ACTIVE","Актив","Фаол ","Faol"),
    STOPPED("STOPPED", "Остановлено", "Тухтатилган", "To'xtatilgan");

    private final String code;
    private final String nameRu;
    private final String nameUzCyrillic;
    private final String nameUzLatin;

    BillingPromotionStatus(String code, String nameRu, String nameUzCyrillic, String nameUzLatin) {
        this.code = code;
        this.nameRu = nameRu;
        this.nameUzCyrillic = nameUzCyrillic;
        this.nameUzLatin = nameUzLatin;
    }

    @JsonCreator
    @SuppressWarnings("unused")
    static BillingPromotionStatus findValue(@JsonProperty("code") String code) {
        return Arrays.stream(BillingPromotionStatus.values()).filter(pt -> pt.code.equals(code)).findFirst().orElseThrow();
    }
}