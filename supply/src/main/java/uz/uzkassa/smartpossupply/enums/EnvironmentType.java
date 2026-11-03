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
public enum EnvironmentType implements Serializable {

    WEB {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Web";
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
    },
    TERMINAL {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Terminal";
                case cyrillic -> "Терминал";
                default -> "Терминал";
            };
        }
    };

    @Schema
    private final String name;

    @Schema
    private final String code;

    EnvironmentType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static EnvironmentType forValue(Map<String, String> value) {
        return EnvironmentType.valueOf(value.get("code"));
    }
}
