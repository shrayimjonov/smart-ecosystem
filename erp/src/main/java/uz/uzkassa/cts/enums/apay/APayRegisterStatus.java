package uz.uzkassa.cts.enums.apay;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.enums.Language;
import uz.uzkassa.cts.security.SecurityUtils;

import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum APayRegisterStatus {
    REGISTERED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ro'yxatdan o'tgan";
                case uz_cyrillic -> "Рўйхатдан ўтган";
                default -> "Зарегистрирован";
            };
        }
    },
    NOT_REGISTERED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ro'yxatdan o'tmagan";
                case uz_cyrillic -> "Рўйхатдан ўтмаган";
                default -> "Не зарегистрирован";
            };
        }
    };

    private final String name;
    private final String code;

    APayRegisterStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static APayRegisterStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown APayRegisterStatus enum value: " + code));
    }

    @JsonCreator
    public static APayRegisterStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for APayRegisterStatus");
    }
}
