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
 * Date: 03.03.2023 15:06
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum TableTypeEnum implements Serializable {
    ORDER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Buyurtma";
                case cyrillic -> "Буюртма";
                default -> "Заказ";
            };
        }
    },
    WAREHOUSE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Ombor";
                case cyrillic -> "Омбор";
                default -> "Склад";
            };
        }
    };
    private final String name;
    private final String code;

    TableTypeEnum() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static TableTypeEnum forValue(Map<String, String> value) {
        return TableTypeEnum.valueOf(value.get("code"));
    }
}
