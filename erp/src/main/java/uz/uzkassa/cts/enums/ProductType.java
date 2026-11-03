package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.security.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum ProductType implements Serializable {

    INVENTORY_ITEM {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ombor mahsuloti";
                case uz_cyrillic -> "Омбор маҳсулоти";
                default -> "Учтённый товар";
            };
        }
    },
    NON_INVENTORY_ITEM {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Omborga kiritilmagan mahsulot";
                case uz_cyrillic -> "Омборга киритилмаган маҳсулот";
                default -> "Неучтённый товар";
            };
        }
    },
    SERVICE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Xizmat";
                case uz_cyrillic -> "Хизмат";
                default -> "Услуга";
            };
        }
    },
    ASSEMBLY_ITEM {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Yig'ma mahsulot";
                case uz_cyrillic -> "Йиғма маҳсулот";
                default -> "Сборочный товар";
            };
        }
    },
    OTHER_CHARGE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Boshqa xarajat";
                case uz_cyrillic -> "Бошқа харажат";
                default -> "Другие обязанности";
            };
        }
    },
    PRODUCT_GROUP {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Mahsulot guruhi";
                case uz_cyrillic -> "Маҳсулот гуруҳи";
                default -> "Группа товаров";
            };
        }
    };

    private final String name;
    private final String code;

    ProductType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static ProductType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown ProductType value: " + code));
    }

    @JsonCreator
    public static ProductType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for ProductType");
    }
}
