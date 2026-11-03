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
public enum PeriodType {

    WEEKLY {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "HAFTALIK";
                case uz_cyrillic -> "ҲAФТAЛИК";
                default -> "ЕЖЕНЕДЕЛЬНИЙ";
            };
        }
    },
    DAILY {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "KUNLIK";
                case uz_cyrillic -> "КУНЛИК";
                default -> "ЕЖЕДНЕВНЫЙ";
            };
        }
    },
    MONTHLY {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "OYLIK";
                case uz_cyrillic -> "ОЙЛИК";
                default -> "ЕЖЕМЕСЯЧНЫЙ";
            };
        }
    },
    YEARLY {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "YILLIK";
                case uz_cyrillic -> "ЙИЛЛИК";
                default -> "ЕЖЕГОДНЫЙ";
            };
        }
    };

    private final String code;
    private final String name;

    PeriodType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static PeriodType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown PeriodType enum value: " + code));
    }

    @JsonCreator
    public static PeriodType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for PeriodType");
    }
}
