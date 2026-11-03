package uz.uzkassa.silen.integration.billing.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PricingModel {
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
