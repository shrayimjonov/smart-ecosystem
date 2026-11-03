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
public enum UzcardConnectionStatus {
    NEW {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Yangi";
                case uz_cyrillic -> "Янги";
                default -> "Новый";
            };
        }
    },
    ACTIVE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Faol";
                case uz_cyrillic -> "Фаол";
                default -> "Активный";
            };
        }
    },
    PENDING {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ko'rib chiqilmoqda";
                case uz_cyrillic -> "Кўриб чиқилмоқда";
                default -> "В ожидании";
            };
        }
    },
    IN_ACTIVE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Faol emas";
                case uz_cyrillic -> "Фаол эмас";
                default -> "Неактивный";
            };
        }
    },
    ERROR {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Xatolik";
                case uz_cyrillic -> "Хатолик";
                default -> "Ошибка";
            };
        }
    };

    private final String name;
    private final String code;

    UzcardConnectionStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static UzcardConnectionStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown CommonStatus enum value: " + code));
    }

    @JsonCreator
    public static UzcardConnectionStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for CommonStatus");
    }
}
