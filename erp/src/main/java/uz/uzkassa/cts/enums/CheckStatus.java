package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.security.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;

@Getter
public enum CheckStatus implements Serializable {

    RIGHT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "To'g'ri";
                case uz_cyrillic -> "Тўғри";
                default -> "Правильно";
            };
        }
    },
    WRONG {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Noto'g'ri";
                case uz_cyrillic -> "Нотўғри";
                default -> "Неправильно";
            };
        }
    };

    private final String code;
    private final String name;

    CheckStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static CheckStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown CheckStatusEnum enum value: " + code));
    }

    @JsonCreator
    public static CheckStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for CheckStatusEnum");
    }
}
