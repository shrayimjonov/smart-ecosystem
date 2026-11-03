package uz.uzkassa.smartpos.integration.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import uz.uzkassa.smartpos.utils.LanguageUtils;

import java.math.BigDecimal;
import java.util.Map;


@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Vat {
    WITHOUT_VAT(null) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "QQS yo'q";
                case cyrillic -> "ҚҚС йўқ";
                default -> "Без НДС";
            };
        }
    },
    VAT_15(new BigDecimal("15.00")) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "QQS 15%";
                case cyrillic -> "ҚҚС 15%";
                default -> "НДС 15%";
            };
        }
    };

    private final String name;
    private final String code;
    private final BigDecimal amount;

    Vat(BigDecimal amount) {
        this.code = name();
        this.name = getName();
        this.amount = amount;
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(LanguageUtils.getLanguage());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static Vat forValue(Map<String, String> value) {
        return Vat.valueOf(value.get("code"));
    }

}
