package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 22.12.2022 16:29
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
@RequiredArgsConstructor
public enum ReceiptPaymentType implements Serializable {
    CASH {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Naqt";
                case cyrillic -> "Нақт";
                default -> "Наличные";
            };
        }
    },

    CARD {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Karta";
                case cyrillic -> "Карта";
                default -> "Карточка";
            };
        }
    },

    UZCARD {
        @Override
        public String getName(LanguageEnum language) {
            return "UZCARD";
        }
    },

    HUMO {
        @Override
        public String getName(LanguageEnum language) {
            return "HUMO";
        }
    },
    APAY {
        @Override
        public String getName(LanguageEnum language) {
            return "A-PAY";
        }
    },
    LOYALTY_CARD {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Sodiqlik karta";
                case cyrillic -> "Содиқлик карта";
                default -> "Карта лояльности";
            };
        }
    },
    NDS {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "QQS";
                case cyrillic -> "ҚҚС";
                default -> "НДС";
            };
        }
    },
    DISCOUNT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Chegirma";
                case cyrillic -> "Чегирма";
                default -> "Скидки";
            };
        }
    },
    EXCISE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Aksiz";
                case cyrillic -> "Аксиз";
                default -> "Акциз";
            };
        }
    },
    OTHER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Boshqa";
                case cyrillic -> "Бошқа";
                default -> "Другой";
            };
        }
    };

    private final String name;
    private final String code;

    ReceiptPaymentType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static ReceiptPaymentType forValue(Map<String, String> value) {
        return ReceiptPaymentType.valueOf(value.get("code"));
    }
}
