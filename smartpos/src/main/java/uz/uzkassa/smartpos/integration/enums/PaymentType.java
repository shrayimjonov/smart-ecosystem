package uz.uzkassa.smartpos.integration.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import uz.uzkassa.smartpos.utils.LanguageUtils;

import java.io.Serializable;
import java.util.Map;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum PaymentType implements Serializable {
    NON_CASH {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Naqd pulsiz";
                case cyrillic -> "Нақд пулсиз";
                default -> "Безналичными";
            };
        }
    },
    CASH {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Naqd";
                case cyrillic -> "Нақд";
                default -> "Наличными";
            };
        }
    },
    APAY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "A-pay";
                case cyrillic -> "A-pay";
                default -> "A-pay платеж";
            };
        }
    },
    TRANSFER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Pul o'tkazma";
                case cyrillic -> "Пул ўтказма";
                default -> "Перечисление";
            };
        }
    },
    CARD {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Karta";
                case cyrillic -> "Карта";
                default -> "Картой";
            };
        }
    };


    private final String name;
    private final String code;

    PaymentType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(LanguageUtils.getLanguage());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static PaymentType forValue(Map<String, String> value) {
        return PaymentType.valueOf(value.get("code"));
    }
}
