package uz.uzkassa.smartpos.integration.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PromocodeType {
    AUTOMATIC("AUTOMATIC", "AUTOMATIC", "AUTOMATIC", "AUTOMATIC"),
    MANUAL("MANUAL", "MANUAL", "MANUAL", "MANUAL");

    private final String nameRu;
    private final String nameUzCyrillic;
    private final String nameUzLatin;
    private final String code;

    PromocodeType(String code, String nameRu, String nameUzCyrillic, String nameUzLatin) {
        this.code = code;
        this.nameRu = nameRu;
        this.nameUzCyrillic = nameUzCyrillic;
        this.nameUzLatin = nameUzLatin;
    }

    @JsonCreator
    static PromocodeType findValue(@JsonProperty("code") String code) {
        return Arrays.stream(PromocodeType.values()).filter(pt -> pt.code.equals(code)).findFirst().orElseThrow();
    }
}

