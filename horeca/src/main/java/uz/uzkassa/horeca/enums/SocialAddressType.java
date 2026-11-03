package uz.uzkassa.horeca.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.io.Serializable;
import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum SocialAddressType implements Serializable {
    FACEBOOK() {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Facebook";
                case cyrillic -> "Фаcебоок";
                case en -> "Facebook";
                default -> "Фейсбук";
            };
        }
    },
    TELEGRAM() {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Telegram";
                case cyrillic -> "Телеграм";
                case en -> "Telegram";
                default -> "Tелеграм";
            };
        }
    },
    INSTAGRAM() {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Instagram";
                case cyrillic -> "Инстаграм";
                case en -> "Instagram";
                default -> "Инстаграм";
            };
        }
    };

    private final String name;
    private final String code;

    SocialAddressType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }

    public static SocialAddressType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown DepartmentType enum value: " + code));
    }

    @JsonCreator
    public static SocialAddressType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for DepartmentType");
    }
}
