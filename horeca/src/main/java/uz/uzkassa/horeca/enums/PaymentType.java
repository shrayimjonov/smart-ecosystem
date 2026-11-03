package uz.uzkassa.horeca.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum PaymentType {
    CARD {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Karta";
                case cyrillic -> "Карта";
                case en -> "Card";
                default -> "Безналичными";
            };
        }
    },
    CASH {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Naqd";
                case cyrillic -> "Нақд";
                case en -> "Cash";
                default -> "Наличными";
            };
        }
    },
    A_PAY {
        @Override
        public String getName(LanguageEnum language) {
            return "A-pay";
        }
    };

    private final String name;
    private final String code;

    PaymentType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }

    public static PaymentType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown PaymentType enum value: " + code));
    }

    @JsonCreator
    public static PaymentType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for PaymentType");
    }
}
