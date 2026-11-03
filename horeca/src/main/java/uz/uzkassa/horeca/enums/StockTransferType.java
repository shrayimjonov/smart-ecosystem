package uz.uzkassa.horeca.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.util.Arrays;


@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
@Getter
public enum StockTransferType {
    BETWEEN_BRANCHES {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Между филиалами";
                case cyrillic -> "Между филиалами";
                default -> "Между филиалами";
            };
        }
    },
    IN_BRANCH {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Внутри филиала";
                case cyrillic -> "Внутри филиала";
                default -> "Внутри филиала";
            };
        }
    };

    private final String name;
    private final String code;

    StockTransferType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }

    public static StockTransferType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown StockTransferType enum value: " + code));
    }

    @JsonCreator
    public static StockTransferType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for StockTransferType");
    }

    public static StockTransferType getByCode(String code) {
        return Arrays.stream(StockTransferType.values()).filter(status -> status.getCode().equals(code)).findFirst().orElse(null);
    }
}
