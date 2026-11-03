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
public enum PaymentStatus implements Serializable {
    NOT_PAID {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "To'lanmagan";
                case cyrillic -> "Тўланмаган";
                case en -> "Not paid";
                default -> "Не оплачено";
            };
        }
    },

    PARTIALLY_PAID {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Qisman to`langan";
                case cyrillic -> "Қисман тўланган";
                case en -> "Partially paid";
                default -> "Частично оплачено";
            };
        }
    },

    PAID {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "To'langan";
                case cyrillic -> "Тўланган";
                case en -> "Paid";
                default -> "Оплачено";
            };
        }
    };

    private final String name;
    private final String code;

    PaymentStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }

    public static PaymentStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown PaymentStatus enum value: " + code));
    }

    @JsonCreator
    public static PaymentStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for PaymentStatus");
    }
}
