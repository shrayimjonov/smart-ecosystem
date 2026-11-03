package uz.uzkassa.horeca.enums.billing;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.io.Serializable;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum PricingModel implements Serializable {
    STANDART("STANDART", "STANDART", "STANDART", "STANDART"),
    PACKAGE("PACKAGE", "PACKAGE", "PACKAGE", "PACKAGE"),
    TIERED("TIERED", "TIERED", "TIERED", "TIERED");

    private final String nameRu;
    private final String nameUzCyrillic;
    private final String nameUzLatin;
    private final String code;

    PricingModel(String code, String nameRu, String nameUzCyrillic, String nameUzLatin) {
        this.code = code;
        this.nameRu = nameRu;
        this.nameUzCyrillic = nameUzCyrillic;
        this.nameUzLatin = nameUzLatin;
    }
}
