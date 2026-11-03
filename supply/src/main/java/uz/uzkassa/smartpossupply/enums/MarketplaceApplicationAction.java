package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MarketplaceApplicationAction implements Serializable {

    CHANGE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "O'zgartirish";
                case cyrillic -> "Ўзгартириш";
                default -> "Изменять";
            };
        }
    },
    REMOVE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Olib tashlash";
                case cyrillic -> "Олиб ташлаш";
                default -> "Снят";
            };
        }
    },
    RETURN {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Qaytarish";
                case cyrillic -> "Қайтариш";
                default -> "Возвращаться";
            };
        }
    };

    private final String name;
    private final String code;

    MarketplaceApplicationAction() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static MarketplaceApplicationAction get(String code) {
        if (!StringUtils.hasText(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElse(null);
    }

    @JsonCreator
    public static MarketplaceApplicationAction fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for MarketplaceApplicationAction");
    }
}
