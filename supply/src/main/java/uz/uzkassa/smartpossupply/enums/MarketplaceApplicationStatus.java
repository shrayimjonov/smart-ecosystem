package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 03.02.2023 13:13
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MarketplaceApplicationStatus implements Serializable {

    NOT_FILLED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "To'ldirilmagan";
                case cyrillic -> "Тўлдирилмаган";
                default -> "Не заполнен";
            };
        }
    },
    FILLED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "To'ldirilgan";
                case cyrillic -> "Тўлдирилган";
                default -> "Заполнен";
            };
        }
    },
    IN_MODERATION {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "O'zgartirishda";
                case cyrillic -> "Ўзгартиришда";
                default -> "На модерации";
            };
        }
    },
    IN_MARKETPLACE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Marketpleysda";
                case cyrillic -> "Маркетплейсда";
                default -> "В Маркетплейсе";
            };
        }
    },
    FAILED_VERIFICATION {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tekshirishdan o'ta olmadi";
                case cyrillic -> "Текширишдан ўта олмади";
                default -> "Не прошел проверку";
            };
        }
    },
    REMOVED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Marketplacedan olib tashlangan";
                case cyrillic -> "Маркетплаcедан олиб ташланган";
                default -> "Снят с маркета";
            };
        }
    };

    private final String name;
    private final String code;

    MarketplaceApplicationStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static MarketplaceApplicationStatus get(String code) {
        if (!StringUtils.hasText(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElse(null);
    }

    @JsonCreator
    public static MarketplaceApplicationStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for MarketplaceApplicationStatus");
    }
}
