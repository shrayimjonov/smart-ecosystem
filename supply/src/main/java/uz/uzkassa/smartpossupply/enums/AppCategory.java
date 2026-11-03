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
public enum AppCategory implements Serializable {
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
                case uz -> "Mobil ilova";
                case cyrillic -> "Мобил илова";
                default -> "Мобильное приложение";
            };
        }
    },
    KKM {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "KKM";
                case cyrillic -> "ККМ";
                default -> "ККМ";
            };
        }
    },
    ALL {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Barchasi";
                case cyrillic -> "Барчаси>";
                default -> "Все";
            };
        }
    };

    private final String name;
    private final String code;

    AppCategory() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static AppCategory forValue(Map<String, String> value) {
        return AppCategory.valueOf(value.get("code"));
    }
}
