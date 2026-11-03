package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 06.07.2023
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum ProductOrigin implements Serializable {

    SELF_PRODUCED(1) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "O‘zi ishlab chiqargan";
                case cyrillic -> "Ўзи ишлаб чиқарган";
                default -> "Сам произвел";
            };
        }
    },
    PURCHASED(2) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Sotib olingan";
                case cyrillic -> "Сотиб олинган";
                default -> "Куплено";
            };
        }
    },
    SERVICE(3) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Xizmat ko‘rsatish";
                case cyrillic -> "Хизмат кўрсатиш";
                default -> "Услуга";
            };
        }
    },
    NOT_PARTICIPATE(4) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Ishtirok etmayman";
                case cyrillic -> "Иштирок этмайман";
                default -> "Не участвую";
            };
        }
    };

    private final Integer id;
    private final String name;
    private final String code;

    ProductOrigin(Integer id) {
        this.id = id;
        this.code = this.name();
        this.name = this.getName();
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static ProductOrigin forValue(Map<String, String> value) {
        return ProductOrigin.valueOf(value.get("code"));
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static ProductOrigin getById(Integer id) {
        return Arrays
            .stream(ProductOrigin.values())
            .filter(productOrigin -> productOrigin.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
}
