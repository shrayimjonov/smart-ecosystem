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
 * Date: 03.02.2023 13:13
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum ProductListType implements Serializable {

    BY_EXPEDITOR {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "BY_EXPEDITOR";
                case cyrillic -> "BY_EXPEDITOR";
                default -> "BY_EXPEDITOR";
            };
        }
    },
    BY_CUSTOMER_BRANCH {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "BY_CUSTOMER_BRANCH";
                case cyrillic -> "BY_CUSTOMER_BRANCH";
                default -> "BY_CUSTOMER_BRANCH";
            };
        }
    };

    @Schema
    private final String name;

    @Schema
    private final String code;

    ProductListType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static ProductListType forValue(Map<String, String> value) {
        return ProductListType.valueOf(value.get("code"));
    }
}
