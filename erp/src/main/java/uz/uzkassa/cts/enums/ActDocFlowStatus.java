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
public enum ActDocFlowStatus {
    IN_CTS {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "TXM da";
                case uz_cyrillic -> "ТХМ да";
                default -> "В ЦТО";
            };
        }
    },
    IN_DO {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Hujjat zavodga kelgan";
                case uz_cyrillic -> "Документ заводга келган";
                default -> "Документ в заводе";
            };
        }
    },
    IN_ACCOUNTING {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Buxgalteriyada";
                case uz_cyrillic -> "Бухгалтерияда";
                default -> "В бухгалтерии";
            };
        }
    },
    WITH_DEFECT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Kamchiligi bor";
                case uz_cyrillic -> "Камчилиги бор";
                default -> "С дефектом";
            };
        }
    };

    private final String code;
    private final String name;

    ActDocFlowStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static ActDocFlowStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown ActDocFlowStatus enum value: " + code));
    }

    @JsonCreator
    public static ActDocFlowStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for ActDocFlowStatus");
    }
}
