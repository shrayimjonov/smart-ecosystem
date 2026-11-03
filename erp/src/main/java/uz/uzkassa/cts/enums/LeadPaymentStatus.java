package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.security.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum LeadPaymentStatus implements Serializable {

    NOT_SPECIFIED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ko`rsatilmagan";
                case uz_cyrillic -> "Корсатилмаган";
                default -> "Не указано";
            };
        }
    },
    PAID {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "To'langan";
                case uz_cyrillic -> "Тўланган";
                default -> "Оплачено";
            };
        }
    },
    NOT_PAID {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "To'lanmagan";
                case uz_cyrillic -> "Тўланмаган";
                default -> "Не оплачено";
            };
        }
    };

    private final String name;
    private final String code;

    LeadPaymentStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static LeadPaymentStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown lead payment status enum value: " + code));
    }

    @JsonCreator
    public static LeadPaymentStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for lead payment status");
    }
}
