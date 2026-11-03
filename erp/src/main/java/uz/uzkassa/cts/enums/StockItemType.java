package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.security.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StockItemType implements Serializable {
    PRODUCT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Mahsulot";
                case uz_cyrillic -> "Маҳсулот";
                default -> "Продукт";
            };
        }
    },
    DEVICE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "NKM";
                case uz_cyrillic -> "ККМ";
                default -> "ККМ";
            };
        }
    },
    FISCAL_MODULE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Fiskal modul";
                case uz_cyrillic -> "Фискал модул";
                default -> "Фискальный модуль";
            };
        }
    },
    ACCESSORY {
        @Override
        public String getName(Language language) {
            return "Аксессуар";
        }
    };


    private final String name;
    private final String code;

    StockItemType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static StockItemType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown StockItemType enum value: " + code));
    }

    @JsonCreator
    public static StockItemType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for StockItemType");
    }
}
