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
public enum PaymentQuote implements Serializable {

    PAID {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "To'langan";
                case uz_cyrillic -> "Тўланган";
                default -> "Оплачен";
            };
        }
    },
    PENDING {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Kutilmoqda";
                case uz_cyrillic -> "Кутилмоқда";
                default -> "В Ожидании";
            };
        }
    },
    CANCELED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Bekor qilingan";
                case uz_cyrillic -> "Бекор қилинган";
                default -> "Отменен";
            };
        }
    };

    private final String code;
    private final String name;

    PaymentQuote() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static PaymentQuote get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown PaymentQuoteEnum enum value: " + code));
    }

    @JsonCreator
    public static PaymentQuote fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for PaymentQuoteEnum");
    }
}
