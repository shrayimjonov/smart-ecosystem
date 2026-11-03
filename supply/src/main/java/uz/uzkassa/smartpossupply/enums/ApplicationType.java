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
 * Date: 06.02.2023 12:34
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum ApplicationType implements Serializable {

    WEB {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Veb";
                case cyrillic -> "Веб";
                default -> "Веб";
            };
        }
    },
    MOBILE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Mobil";
                case cyrillic -> "Мобил";
                default -> "Мобил";
            };
        }
    };


    private final String name;
    private final String code;


    ApplicationType() {
        this.name = this.getName();
        this.code = this.name();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static ApplicationType forValue(Map<String, String> value) {
        return ApplicationType.valueOf(value.get("code"));
    }
}
