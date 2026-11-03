package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.security.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PaymentQuoteTypeEnum implements Serializable {
    FULL_PAYMENT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz_latin -> "100% lik to'lov Prime";
                case uz_cyrillic -> "100% лик тўлов Prime";
                default -> "100% оплата Prime";
            };
        }
    },
    LEASING_PRIME {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz_latin -> "Prime bo'lib to'lash";
                case uz_cyrillic -> "Prime бўлиб тўлаш";
                default -> "Рассрочка Prime";
            };
        }
    },
    A_PAY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz_latin -> "A-pay";
                case uz_cyrillic -> "A-pay";
                default -> "A-pay";
            };
        }
    };

    private final String code;
    private final String name;

    PaymentQuoteTypeEnum() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static PaymentQuoteTypeEnum get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown PaymentQuoteTypeEnum enum value: " + code));
    }

    @JsonCreator
    public static PaymentQuoteTypeEnum fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for PaymentQuoteTypeEnum");
    }
}
