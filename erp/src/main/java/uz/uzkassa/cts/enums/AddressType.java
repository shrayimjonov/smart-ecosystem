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
public enum AddressType {

    TEMPORARY {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Vaqtinchalik";
                case uz_cyrillic -> "Вақтинчалик";
                default -> "Временный";
            };
        }
    },
    PERMANENT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Doimiy";
                case uz_cyrillic -> "Доимий";
                default -> "Постоянный";
            };
        }
    },
    LEGAL {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Yuridik";
                case uz_cyrillic -> "Юридик";
                default -> "Юридический";
            };
        }
    },
    PHYSICAL {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Jismoniy";
                case uz_cyrillic -> "Жисмоний";
                default -> "Физический";
            };
        }
    };

    private final String code;
    private final String name;

    AddressType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static AddressType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown AddressTypeEnum enum value: " + code));
    }

    @JsonCreator
    public static AddressType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for AddressTypeEnum");
    }
}
