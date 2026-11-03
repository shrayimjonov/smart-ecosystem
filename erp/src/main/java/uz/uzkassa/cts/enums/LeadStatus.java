package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.security.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum LeadStatus implements Serializable {

    NEW {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Yangi";
                case uz_cyrillic -> "Янги";
                default -> "Новый";
            };
        }
    },
    TARGET {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Haqiqiy";
                case uz_cyrillic -> "Ҳақиқий";
                default -> "Целевой";
            };
        }
    },
    NOT_TARGET {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Haqiqiy emas";
                case uz_cyrillic -> "Ҳақиқий эмас";
                default -> "Не целевой";
            };
        }
    };

    private final String name;
    private final String code;

    LeadStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static LeadStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown Customer request status enum value: " + code));
    }

    @JsonCreator
    public static LeadStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for Customer request status");
    }
}
