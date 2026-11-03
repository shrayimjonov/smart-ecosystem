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
import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum ShapeType implements Serializable {
    RECTANGLE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "To'rtburchak";
                case cyrillic -> "Тўртбурчак";
                case en -> "Rectangle";
                default -> "Прямоугольник";
            };
        }
    },
    SQUARE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Kvadrat";
                case cyrillic -> "Квадрат";
                case en -> "Square";
                default -> "Квадрат";
            };
        }
    },
    CIRCLE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Айлана";
                case cyrillic -> "Квадрат";
                case en -> "Circle";
                default -> "Круг";
            };
        }
    },
    OVAL {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz, en -> "Oval";
                default -> "Овал";
            };
        }
    };

    private final String name;
    private final String code;

    ShapeType() {
        this.code = this.name();
        this.name = getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }

    public static ShapeType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown ShapeType enum value: " + code));
    }

    @JsonCreator
    public static ShapeType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for ShapeType");
    }

    public static ShapeType getByCode(String code) {
        return Arrays.stream(ShapeType.values()).filter(shapeType -> shapeType.getCode().equals(code)).findFirst().orElse(null);
    }
}
