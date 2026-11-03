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
public enum CallStatus implements Serializable {

    PROCESSED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Qayta ishlangan";
                case uz_cyrillic -> "Қайта ишланган";
                default -> "Обработан";
            };
        }
    },
    NOT_PROCESSED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ishlov berilmagan";
                case uz_cyrillic -> "Ишлов берилмаган";
                default -> "Не обработан";
            };
        }
    },
    PHONE_NOT_ACTIVE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Telefon faol emas";
                case uz_cyrillic -> "Телефон фаол эмас";
                default -> "Номер не активен";
            };
        }
    };

    private final String name;
    private final String code;

    CallStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static CallStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown Call status enum value: " + code));
    }

    @JsonCreator
    public static CallStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for Call status");
    }
}
