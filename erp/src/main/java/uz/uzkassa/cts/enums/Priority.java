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
public enum Priority implements Serializable {

    LOW {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Past";
                case uz_cyrillic -> "Паст";
                default -> "Низкий";
            };
        }
    },
    MEDIUM {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "O'rtacha";
                case uz_cyrillic -> "Ўртача";
                default -> "Средний";
            };
        }
    },
    HIGH {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Yuqori";
                case uz_cyrillic -> "Юқори";
                default -> "Высокий";
            };
        }
    };

    private final String code;
    private final String name;

    Priority() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static Priority get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown PriorityEnum enum value: " + code));
    }

    @JsonCreator
    public static Priority fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for PriorityEnum");
    }
}
