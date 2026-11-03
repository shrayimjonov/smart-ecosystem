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
public enum BankType {
    TRASTBANK {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Trast bank";
                case uz_cyrillic -> "Траст банк";
                default -> "Траст банк";
            };
        }
    },
    HAMKORBANK {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Hamkor bank";
                case uz_cyrillic -> "Ҳамкор банк";
                default -> "Хамкор банк";
            };
        }
    };

    private final String name;
    private final String code;

    BankType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static BankType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown BankType enum value: " + code));
    }

    @JsonCreator
    public static BankType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for BankType");
    }
}