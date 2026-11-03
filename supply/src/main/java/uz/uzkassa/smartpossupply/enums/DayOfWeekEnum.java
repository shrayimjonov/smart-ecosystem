package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.Map;

/**
 * Powered by Shuxratjon Rayimjonov
 * Date: 27.01.2023 12:07
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum DayOfWeekEnum implements Serializable {
    MONDAY(DayOfWeek.MONDAY) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Du";
                case cyrillic -> "Ду";
                default -> "Пн";
            };
        }
    },
    TUESDAY(DayOfWeek.TUESDAY) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Se";
                case cyrillic -> "Се";
                default -> "Вт";
            };
        }
    },
    WEDNESDAY(DayOfWeek.WEDNESDAY) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Chor";
                case cyrillic -> "Чор";
                default -> "Ср";
            };
        }
    },
    THURSDAY(DayOfWeek.THURSDAY) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Pay";
                case cyrillic -> "Пай";
                default -> "Чт";
            };
        }
    },
    FRIDAY(DayOfWeek.FRIDAY) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Ju";
                case cyrillic -> "Жу";
                default -> "Пт";
            };
        }
    },
    SATURDAY(DayOfWeek.SATURDAY) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Shan";
                case cyrillic -> "Шан";
                default -> "Сб ";
            };
        }
    },
    SUNDAY(DayOfWeek.SUNDAY) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yak";
                case cyrillic -> "Як";
                default -> "Вс";
            };
        }
    };
    public final String code;
    public final String name;
    public final DayOfWeek dayOfWeek;

    DayOfWeekEnum(DayOfWeek dayOfWeek) {
        this.code = this.name();
        this.name = this.getName();
        this.dayOfWeek = dayOfWeek;
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    public static DayOfWeekEnum forValue(Map<String, String> value) {
        return DayOfWeekEnum.valueOf(value.get("code"));
    }
}
