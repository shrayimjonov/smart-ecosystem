package uz.uzkassa.cts.enums.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BillingDocumentType {
    PUBLIC_OFFER("PUBLIC_OFFER", "Публичная оферта", "Публичная оферта", "Публичная оферта"),
    CONTRACT("CONTRACT", "ДОГОВОР", "ДОГОВОР", "ДОГОВОР");

    private final String nameRu;
    private final String nameUzCyrillic;
    private final String nameUzLatin;
    private final String code;

    BillingDocumentType(String code, String nameRu, String nameUzCyrillic, String nameUzLatin) {
        this.nameRu = nameRu;
        this.nameUzCyrillic = nameUzCyrillic;
        this.nameUzLatin = nameUzLatin;
        this.code = code;
    }

    @JsonCreator
    static BillingDocumentType findValue(@JsonProperty("code") String code) {
        return Arrays.stream(BillingDocumentType.values()).filter(pt -> pt.code.equals(code)).findFirst().orElseThrow();
    }
}
