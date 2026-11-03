package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ChecklistStatus implements Serializable {
    CREATED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yaratilgan";
                case cyrillic -> "Яратилган";
                default -> "Созданный";
            };
        }
    },
    IN_PROGRESS {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Jarayonda";
                case cyrillic -> "Жараёнда";
                default -> "В процессе";
            };
        }
    },
    COMPLETED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tugallangan";
                case cyrillic -> "Тугалланган";
                default -> "Завершенный";
            };
        }
    };

    private final String name;
    private final String code;

    ChecklistStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static ChecklistStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown ChecklistStatus enum value: " + code));
    }

    @JsonCreator
    public static ChecklistStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for ChecklistStatus");
    }
}
