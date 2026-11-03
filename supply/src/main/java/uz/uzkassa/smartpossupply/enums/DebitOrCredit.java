package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.io.Serializable;
import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum DebitOrCredit implements Serializable {
    DEBIT("DEBIT", "DEBIT", "DEBIT", "DEBIT"),
    CREDIT("CREDIT", "CREDIT", "CREDIT", "CREDIT");

    private final String nameRu;
    private final String nameUzCyrillic;
    private final String nameUzLatin;
    private final String code;

    DebitOrCredit(String code, String nameRu, String nameUzCyrillic, String nameUzLatin) {
        this.code = code;
        this.nameRu = nameRu;
        this.nameUzCyrillic = nameUzCyrillic;
        this.nameUzLatin = nameUzLatin;
    }

    @JsonCreator
    static DebitOrCredit findValue(@JsonProperty("code") String code) {
        return Arrays.stream(DebitOrCredit.values()).filter(pt -> pt.getCode().equals(code)).findFirst().orElse(null);
    }
}
