package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;


@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum LauncherStatus implements Serializable {

    BLOCK {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Bloklangan";
                case cyrillic -> "Блокланган";
                default -> "Заблокировано";
            };
        }
    },
    UNBLOCK {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Blokdan chiqarilgan";
                case cyrillic -> "Блокдан чиқарилган";
                default -> "Разблокировано";
            };
        }
    };

    private final String name;

    private final String code;

    LauncherStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static LauncherStatus get(String code) {
        if (!StringUtils.hasText(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown LauncherStatus enum value: " + code));
    }

    @JsonCreator
    public static LauncherStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for LauncherStatus");
    }
}
