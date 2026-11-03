package uz.uzkassa.smartpos.integration.cto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import uz.uzkassa.smartpos.integration.enums.LanguageEnum;
import uz.uzkassa.smartpos.utils.LanguageUtils;

import java.io.Serializable;
import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum LeadStatusEnum implements Serializable {

    NEW {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yangi";
                case cyrillic -> "Янги";
                default -> "Новый";
            };
        }
    },
    TARGET {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Haqiqiy";
                case cyrillic -> "Ҳақиқий";
                default -> "Целевой";
            };
        }
    },
    NOT_TARGET {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Haqiqiy emas";
                case cyrillic -> "Ҳақиқий эмас";
                default -> "Не целевой";
            };
        }
    };

    private final String code;

    LeadStatusEnum() {
        this.code = this.name();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(LanguageUtils.getLanguage());
    }

    public static LeadStatusEnum get(String code) {
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown Customer request status enum value: " + code));
    }

    @JsonCreator
    public static LeadStatusEnum fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for Customer request status");
    }
}
