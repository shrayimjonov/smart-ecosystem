package uz.uzkassa.horeca.enums.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum BillingTariffType {
    START("START", "Старт", "Старт", "Старт"),
    COMFORT("COMFORT", "Комфорт", "Комфорт", "Комфорт"),
    FULL("FULL", "Фулл", "Фулл", "Фулл"),
    HAMKOR("HAMKOR", "HAMKOR", "HAMKOR", "HAMKOR"),
    SIMPLE("SIMPLE", "простой", "простой", "простой");

    private final String code;
    private final String nameRu;
    private final String nameUzCyrillic;
    private final String nameUzLatin;

    BillingTariffType(String code, String nameRu, String nameUzCyrillic, String nameUzLatin) {
        this.code = code;
        this.nameRu = nameRu;
        this.nameUzCyrillic = nameUzCyrillic;
        this.nameUzLatin = nameUzLatin;
    }

    @JsonCreator
    public static BillingTariffType forValue(@JsonProperty("code") String code) {
        return Arrays.stream(values()).filter(e -> e.getCode().equals(code)).findFirst().orElse(null);
    }
}

