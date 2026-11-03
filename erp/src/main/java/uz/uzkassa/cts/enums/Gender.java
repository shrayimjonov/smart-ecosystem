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
public enum Gender {

    MALE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Erkak";
                case uz_cyrillic -> "Эркак";
                default -> "Мужской";
            };
        }
    },
    FEMALE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ayol";
                case uz_cyrillic -> "Аёл";
                default -> "Женский";
            };
        }
    };

    private final String name;
    private final String code;

    Gender() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static Gender get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown Gender enum value: " + code));
    }

    @JsonCreator
    public static Gender fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for Gender");
    }
}
