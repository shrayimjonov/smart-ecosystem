package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;


@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum ChecklistConditionType implements Serializable {
    AVAILABILITY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Mahsulot assortimentida mavjudligi";
                case cyrillic -> "Маҳсулот ассортиментида мавжудлиги";
                default -> "Присуствыя продукта в ассортименте";
            };
        }
    },
    PRESENTATION {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Mahsulot taqdimoti. Merchandising";
                case cyrillic -> "Маҳсулот тақдимоти. Мерчандисинг";
                default -> "Выклодька продукта. Мерчендайзинг";
            };
        }
    },
    CATEGORY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Kategoriya";
                case cyrillic -> "Категория";
                default -> "Категория";
            };
        }
    },
    EQUIPMENT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Uskunalar";
                case cyrillic -> "Ускуналар";
                default -> "Оборудование";
            };
        }
    },
    RECOMMEND_PRICE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tavsiya etilgan narx";
                case cyrillic -> "Тавсия этилган нарх";
                default -> "Рекомендованная цена";
            };
        }
    },
    PRODUCT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Mahsulot";
                case cyrillic -> "Продукт";
                default -> "Продукт";
            };
        }
    };

    private final String name;
    private final String code;

    ChecklistConditionType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static ChecklistConditionType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown ChecklistConditionType enum value: " + code));
    }

    @JsonCreator
    public static ChecklistConditionType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for ChecklistConditionType");
    }
}
