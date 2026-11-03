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
public enum CategoryType implements Serializable {
    FOOD {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Ovqat";
                case cyrillic -> "Овқат";
                case en -> "Food";
                default -> "Блюда";
            };
        }
    },
    PRODUCT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tovar";
                case cyrillic -> "Товар";
                case en -> "Product";
                default -> "Товар";
            };
        }
    },
    INGREDIENT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Ingredient";
                case cyrillic -> "Ингредиент";
                case en -> "Ingredient";
                default -> "Ингредиенты";
            };
        }
    },
    SEMI_FINISHED_PRODUCT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yarim tayyor mahsulotlar";
                case cyrillic -> "Ярим тайёр маҳсулотлар";
                case en -> "Semi-finished products";
                default -> "Полуфабрикаты";
            };
        }
    },
    CATERING_SERVICE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Umumiy ovqatlanish korxonalarida boshqa texnik xizmat ko'rsatish";
                case cyrillic -> "Умумий овқатланиш корхоналарида бошқа техник хизмат кўрсатиш";
                case en -> "Other maintenance services in catering establishments";
                default -> "Прочие услуги сервисного обслуживания в заведении общественного питания";
            };
        }
    },
    OTHER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Boshqa";
                case cyrillic -> "Бошқа";
                case en -> "Other";
                default -> "Прочее";
            };
        }
    },
    COMPENSATION {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Jarima";
                case cyrillic -> "Жарима";
                case en -> "Compensation";
                default -> "Штраф";
            };
        }
    };

    private final String name;
    private final String code;

    CategoryType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }

    public static CategoryType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown CategoryType enum value: " + code));
    }

    @JsonCreator
    public static CategoryType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for CategoryType");
    }

    public static CategoryType getByCode(String code) {
        return Arrays.stream(CategoryType.values()).filter(categoryType -> categoryType.getCode().equals(code)).findFirst().orElse(null);
    }
}
