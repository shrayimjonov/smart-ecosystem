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
public enum ShiftStatus implements Serializable {
    OPEN {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Ochiq";
                case cyrillic -> "Очиқ";
                case en -> "Open";
                default -> "Открыть";
            };
        }
    },
    CLOSED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yopilgan";
                case cyrillic -> "Ёпилган";
                case en -> "Closed";
                default -> "Закрыто";
            };
        }
    };

    private final String name;

    private final String code;

    ShiftStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }

    public static ShiftStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown ShiftStatus enum value: " + code));
    }

    @JsonCreator
    public static ShiftStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for ShiftStatus");
    }

    public static ShiftStatus getByCode(String code) {
        return Arrays.stream(ShiftStatus.values()).filter(status -> status.getCode().equals(code)).findFirst().orElse(null);
    }
}
