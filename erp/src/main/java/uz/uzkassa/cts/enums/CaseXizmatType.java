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
public enum CaseXizmatType {
    CASE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Keys";
                case uz_cyrillic -> "Кейс";
                default -> "Кейс";
            };
        }
    },
    REGISTER_TERMINAL {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Terminalni royxatdan otish";
                case uz_cyrillic -> "Терминални ройхатдан отиш";
                default -> "Регистрация Терминала";
            };
        }
    },
    REGISTER_FISCAL {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Fiskal NKM royxatdan otish";
                case uz_cyrillic -> "Фискал НКМ ройхатдан отиш";
                default -> "Регистрация Фискального ККМ";
            };
        }
    };

    private final String name;
    private final String code;

    CaseXizmatType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static CaseXizmatType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown CaseXizmatTypeEnum enum value: " + code));
    }

    @JsonCreator
    public static CaseXizmatType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for CaseXizmatTypeEnum");
    }
}
