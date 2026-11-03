package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 22.12.2022 12:39
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum CardType implements Serializable {
    CUMULATIVE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "To`planib boriladigan";
                case cyrillic -> "Тўпланиб бориладиган";
                default -> "Накопительная";
            };
        }
    },
    DISCOUNT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Chegirmali";
                case cyrillic -> "Чегирмали";
                default -> "Скидочная";
            };
        }
    },
    CREDIT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Kredit";
                case cyrillic -> "Кредит";
                default -> "Кредитная";
            };
        }
    };

    CardType() {
        this.name = this.getName();
        this.code = this.name;
    }

    private final String name;

    private final String code;

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static CardType get(String code) {
        if (!StringUtils.hasText(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown CardType enum value: " + code));
    }

    @JsonCreator
    public static CardType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for CardType");
    }
}
