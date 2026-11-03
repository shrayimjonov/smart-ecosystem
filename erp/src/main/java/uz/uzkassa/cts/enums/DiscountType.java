package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.security.SecurityUtils;

import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum DiscountType {

    PERCENT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Foiz";
                case uz_cyrillic -> "Фоиз";
                default -> "Процент";
            };
        }
    },
    AMOUNT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Qiymat";
                case uz_cyrillic -> "Қиймат";
                default -> "Стоимость";
            };
        }
    };

    private final String code;
    private final String name;

    DiscountType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static DiscountType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown DiscountType enum value: " + code));
    }

    @JsonCreator
    public static DiscountType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for DiscountType");
    }
}
