package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Map;


@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum ProductSerialStatus implements Serializable {

    CREATED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yaratilgan";
                case cyrillic -> "Яратилган";
                default -> "Создан";
            };
        }
    },
    PRODUCED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Ishlab chiqarilgan";
                case cyrillic -> "Ишлаб чиқарилган";
                default -> "Произведено";
            };
        }
    };

    private final String name;
    private final String code;

    ProductSerialStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static ProductSerialStatus forValue(Map<String, String> value) {
        return ProductSerialStatus.valueOf(value.get("code"));
    }
}

