package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 16.12.2024
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
@Getter
public enum DiscountValueType implements Serializable {
    PERCENTAGE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "PERCENTAGE";
                case cyrillic -> "PERCENTAGE";
                default -> "PERCENTAGE";
            };
        }
    },
    FIXED_PRICE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "FIXED_PRICE";
                case cyrillic -> "FIXED_PRICE";
                default -> "FIXED_PRICE";
            };
        }
    },
    QTY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "QTY";
                case cyrillic -> "QTY";
                default -> "QTY";
            };
        }
    };
    private final String name;
    private final String code;

    DiscountValueType() {
        this.name = this.getName();
        this.code = name();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static DiscountValueType forValue(Map<String, String> value) {
        return DiscountValueType.valueOf(value.get("code"));
    }
}