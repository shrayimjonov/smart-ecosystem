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
public enum DiscountType implements Serializable {
    SIMPLE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "SIMPLE";
                case cyrillic -> "SIMPLE";
                default -> "SIMPLE";
            };
        }
    },
    MULTI_RANGE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "MULTI_RANGE";
                case cyrillic -> "MULTI_RANGE";
                default -> "MULTI_RANGE";
            };
        }
    };
    private final String name;
    private final String code;

    DiscountType() {
        this.name = this.getName();
        this.code = name();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static DiscountType forValue(Map<String, String> value) {
        return DiscountType.valueOf(value.get("code"));
    }
}