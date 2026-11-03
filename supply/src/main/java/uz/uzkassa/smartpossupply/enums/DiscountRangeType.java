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
public enum DiscountRangeType implements Serializable {
    BY_QTY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "BY_QTY";
                case cyrillic -> "BY_QTY";
                default -> "BY_QTY";
            };
        }
    },
    BY_TOTAL_PRICE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "BY_TOTAL_PRICE";
                case cyrillic -> "BY_TOTAL_PRICE";
                default -> "BY_TOTAL_PRICE";
            };
        }
    };
    private final String name;
    private final String code;

    DiscountRangeType() {
        this.name = this.getName();
        this.code = name();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static DiscountRangeType forValue(Map<String, String> value) {
        return DiscountRangeType.valueOf(value.get("code"));
    }
}