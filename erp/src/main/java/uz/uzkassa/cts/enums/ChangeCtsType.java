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
public enum ChangeCtsType {
    LEFT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ketdi";
                case uz_cyrillic -> "Кетди";
                default -> "Ушел";
            };
        }
    },
    CAME {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Keldi";
                case uz_cyrillic -> "Келди";
                default -> "Пришел";
            };
        }
    };

    private final String name;
    private final String code;

    ChangeCtsType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static ChangeCtsType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown ChangeCtsType enum value: " + code));
    }

    @JsonCreator
    public static ChangeCtsType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for ChangeCtsType");
    }
}
