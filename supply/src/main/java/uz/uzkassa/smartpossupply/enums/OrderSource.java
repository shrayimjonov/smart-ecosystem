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
 * Date: 22.05.2024
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum OrderSource implements Serializable {
    MANUAL {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Qo'lda";
                case cyrillic -> "Қўлда";
                default -> "Вручную";
            };
        }
    },
    PLAN_ROUTE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Marshrut";
                case cyrillic -> "Маршрут";
                default -> "Маршрут";
            };
        }
    },
    ONE_C {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "1C";
                case cyrillic -> "1C";
                default -> "1C";
            };
        }
    },
    HIPPO {
        @Override
        public String getName(LanguageEnum language) {
            return "Hippo";
        }
    },
    TELEGRAM {
        @Override
        public String getName(LanguageEnum language) {
            return "Telegram";
        }
    },
    FILE_IMPORT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Fayl import";
                case cyrillic -> "Файл импорт";
                default -> "Импорт файла";
            };
        }
    },
    MARKETPLACE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Marketpleys";
                case cyrillic -> "Маркетплейс";
                default -> "Маркетплейс";
            };
        }
    };

    private final String name;
    private final String code;

    OrderSource() {
        this.code = name();
        this.name = getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static Vat forValue(Map<String, String> value) {
        return Vat.valueOf(value.get("code"));
    }
}
