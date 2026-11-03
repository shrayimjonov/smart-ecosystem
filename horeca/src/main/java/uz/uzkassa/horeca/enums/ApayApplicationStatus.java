package uz.uzkassa.horeca.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ApayApplicationStatus {
    NEW {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.ru) {
                return "Новый";
            }
            return "Yangi";
        }
    },
    PENDING {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.ru) {
                return "В ожидании";
            }
            return "Kutilmoqda";
        }
    },
    APPROVED {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.ru) {
                return "Одобренный";
            }
            return "Tasdiqlangan";
        }
    };

    private final String name;
    private final String code;

    ApayApplicationStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }

    public static ApayApplicationStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown ApayApplicationStatus enum value: " + code));
    }

    @JsonCreator
    public static ApayApplicationStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for ApayApplicationStatus");
    }
}
