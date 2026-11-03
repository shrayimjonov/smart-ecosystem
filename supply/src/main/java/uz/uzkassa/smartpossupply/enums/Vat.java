package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;


@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum Vat implements Serializable {
    WITHOUT_VAT(null),
    VAT_0(BigDecimal.ZERO),
    VAT_6(new BigDecimal("6")),
    VAT_12(new BigDecimal("12")),
    VAT_15(new BigDecimal("15"));

    private final String code;
    private final BigDecimal amount;

    Vat(BigDecimal amount) {
        this.code = name();
        this.amount = amount;
    }

    public String getName(LanguageEnum language) {
        if (this == WITHOUT_VAT) {
            return switch (language) {
                case uz -> "QQS yo'q";
                case cyrillic -> "ҚҚС йўқ";
                default -> "Без НДС";
            };
        }
        String percent = amount.stripTrailingZeros().toPlainString() + "%";
        return switch (language) {
            case uz -> "QQS " + percent;
            case cyrillic -> "ҚҚС " + percent;
            default -> "НДС " + percent;
        };
    }

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static Vat forValue(Map<String, String> value) {
        return Vat.valueOf(value.get("code"));
    }

    public static Vat getByAmount(BigDecimal amount) {
        if (amount == null || amount.signum() == 0) {
            return Vat.WITHOUT_VAT;
        }
        return Arrays.stream(values())
            .filter(vat -> vat.amount != null && vat.amount.compareTo(amount) == 0)
            .findFirst()
            .orElse(Vat.WITHOUT_VAT);
    }
}