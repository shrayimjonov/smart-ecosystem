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

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 24.10.2024
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum PhotoType implements Serializable {
    LOW {
        @Override
        public String getName(LanguageEnum language) {
            return "LOW";
        }
    },
    MEDIUM {
        @Override
        public String getName(LanguageEnum language) {
            return "MEDIUM";
        }
    },
    HIGH {
        @Override
        public String getName(LanguageEnum language) {
            return "HIGH";
        }
    },
    ORIGINAL {
        @Override
        public String getName(LanguageEnum language) {
            return "ORIGINAL";
        }
    };

    private final String name;
    private final String code;

    PhotoType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public static PhotoType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown PhotoType enum value: " + code));
    }

    @JsonCreator
    public static PhotoType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for PhotoType");
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }
}
