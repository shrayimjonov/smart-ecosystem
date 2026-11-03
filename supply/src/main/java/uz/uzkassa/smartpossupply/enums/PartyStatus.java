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
public enum PartyStatus implements Serializable {
    DRAFT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Qoralama";
                case cyrillic -> "Қоралама";
                default -> "Черновик";
            };
        }
    }, CREATED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Aggregatsiya qilindi";
                case cyrillic -> "Аггрегация қилинди";
                default -> "Агрегирован";
            };
        }
    }, ERROR {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Ошибка";
                case cyrillic -> "Ошибка";
                default -> "Ошибка";
            };
        }
    }, INCOME {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Kirim qilindi";
                case cyrillic -> "Кирим қилинди";
                default -> "Оприходован";
            };
        }
    }, SHIPPED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yuborildi";
                case cyrillic -> "Юборилди";
                default -> "Отгружен";
            };
        }
    }, ADJUSTMENT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Корректировка";
                case cyrillic -> "Корректировка";
                default -> "Корректировка";
            };
        }
    }, BROADCAST {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Передача";
                case cyrillic -> "Передача";
                default -> "Передача";
            };
        }
    };

    private final String name;
    private final String code;

    PartyStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static PartyStatus forValue(Map<String, String> value) {
        return PartyStatus.valueOf(value.get("code"));
    }
}
