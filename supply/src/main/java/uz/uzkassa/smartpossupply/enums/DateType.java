package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Map;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
@Getter
public enum DateType implements Serializable {
    DAY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "kun";
                case cyrillic -> "день";
                default -> "день";
            };
        }
    },
    WEEK {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "hafta";
                case cyrillic -> "неделю";
                default -> "неделю";
            };
        }
    },
    MONTH {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "oy";
                case cyrillic -> "месяц";
                default -> "месяц";
            };
        }
    },
    YEAR {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "yil";
                case cyrillic -> "год";
                default -> "год";
            };
        }
    };

    private final String name;
    private final String code;

    DateType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static DateType forValue(Map<String, String> value) {
        return DateType.valueOf(value.get("code"));
    }
}
