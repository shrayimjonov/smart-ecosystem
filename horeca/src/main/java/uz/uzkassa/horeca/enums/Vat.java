package uz.uzkassa.horeca.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;


@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum Vat implements Serializable {
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
    VAT_0(BigDecimal.ZERO) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "QQS 0%";
                case cyrillic -> "ҚҚС 0%";
                default -> "НДС 0%";
            };
        }
    },
    VAT_12(new BigDecimal("12")) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "QQS 12%";
                case cyrillic -> "ҚҚС 12%";
                default -> "НДС 12%";
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

    public static Vat getByAmount(BigDecimal amount) {
        if (amount == null) {
            return Vat.WITHOUT_VAT;
        } else if (amount.compareTo(Vat.VAT_12.getAmount()) == 0) {
            return Vat.VAT_12;
        } else {
            return Vat.WITHOUT_VAT;
        }
    }

    public static BigDecimal getDefaultVatRate() {
        return Vat.VAT_12.getAmount();
    }

    public abstract String getName(LanguageEnum languageEnum);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }

    public static Vat get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown Vat enum value: " + code));
    }

    @JsonCreator
    public static Vat fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for Vat");
    }
    public static Vat getByCode(String code) {
        return Arrays.stream(Vat.values()).filter(vat -> vat.getCode().equals(code)).findFirst().orElse(null);
    }
}
