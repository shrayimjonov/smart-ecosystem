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
public enum PersonalIdentityType {

    PASSPORT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Pasport";
                case uz_cyrillic -> "Паспорт";
                default -> "Паспорт";
            };
        }
    },
    DRIVER_LICENSE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Haydovchilik guvohnomasi";
                case uz_cyrillic -> "Ҳайдовчилик гувоҳномаси";
                default -> "Водительские права";
            };
        }
    };

    private final String name;
    private final String code;

    PersonalIdentityType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static PersonalIdentityType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown IdentityDocumentType enum value: " + code));
    }

    @JsonCreator
    public static PersonalIdentityType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for IdentityDocumentType");
    }
}
