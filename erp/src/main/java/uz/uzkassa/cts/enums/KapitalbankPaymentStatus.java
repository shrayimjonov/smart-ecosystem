package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.security.SecurityUtils;

import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum KapitalbankPaymentStatus {

    CREATED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "YARATILDI";
                case uz_cyrillic -> "ЯРАТИЛДИ";
                default -> "СОЗДАНО";
            };
        }
    },
    WAITING_FOR_APPROVAL {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "TASDIQLASHNI KUTMOQDA";
                case uz_cyrillic -> "ТАСДИҚЛАШНИ КУТМОҚДА";
                default -> "ОЖИДАНИЕ ПОДТВЕРЖДЕНИЯ";
            };
        }
    },
    CONFIRMED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "TASDIQLANGAN";
                case uz_cyrillic -> "ТAСДИҚЛAНГAН";
                default -> "ПОДТВЕРЖДЕН";
            };
        }
    },
    REJECTED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "RAD ETILGAN";
                case uz_cyrillic -> "РАД ЭТИЛГАН";
                default -> "ОТКЛОНЕН";
            };
        }
    };

    private final String code;
    private final String name;

    KapitalbankPaymentStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static KapitalbankPaymentStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown KapitalbankPaymentStatus enum value: " + code));
    }

    @JsonCreator
    public static KapitalbankPaymentStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for KapitalbankPaymentStatus");
    }
}
