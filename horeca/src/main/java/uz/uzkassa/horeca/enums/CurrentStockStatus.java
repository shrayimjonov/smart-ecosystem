package uz.uzkassa.horeca.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.io.Serializable;
import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum CurrentStockStatus implements Serializable {
    OPEN {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "OPEN";
                case cyrillic -> "OPEN";
                case en -> "OPEN";
                default -> "OPEN";
            };
        }
    },
    CLOSED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "CLOSED";
                case cyrillic -> "CLOSED";
                case en -> "CLOSED";
                default -> "CLOSED";
            };
        }
    },
    PAUSED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "PAUSED";
                case cyrillic -> "PAUSED";
                case en -> "PAUSED";
                default -> "PAUSED";
            };
        }
    };

    private final String name;

    private final String code;

    CurrentStockStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }

    public static CurrentStockStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown CurrentStockStatus enum value: " + code));
    }

    @JsonCreator
    public static CurrentStockStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for CurrentStockStatus");
    }

}
