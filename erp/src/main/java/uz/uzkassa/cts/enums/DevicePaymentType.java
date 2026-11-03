package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.security.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum DevicePaymentType implements Serializable {

    FULL_PAYMENT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "100% lik to'lov PRIME ONLINE TECHNOLOGY";
                case uz_cyrillic -> "100% лик тўлов PRIME ONLINE TECHNOLOGY";
                default -> "100% оплата PRIME ONLINE TECHNOLOGY";
            };
        }
    },
    LEASING {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "SPECIAL TEXNO SOFT bo'lib to'lash";
                case uz_cyrillic -> "SPECIAL TEXNO SOFT бўлиб тўлаш";
                default -> "Рассрочка от SPECIAL TEXNO SOFT";
            };
        }
    },
    LEASING_PRIME {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Prime bo'lib to'lash";
                case uz_cyrillic -> "Prime бўлиб тўлаш";
                default -> "Рассрочка от Prime";
            };
        }
    },
    STS_TRADE_IN {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "SPECIAL TEXNO SOFT Treyd-in";
                case uz_cyrillic -> "SPECIAL TEXNO SOFT Трейд-ин";
                default -> "SPECIAL TEXNO SOFT Трейд-ин";
            };
        }
    },
    RENT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "SPECIAL TEXNO SOFT Ijara";
                case uz_cyrillic -> "SPECIAL TEXNO SOFT Ижара";
                default -> "SPECIAL TEXNO SOFT Аренда";
            };
        }
    },
    REREGISTRATION {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Qayta ro'yxatga qo'yish";
                case uz_cyrillic -> "Қайта рўйхатга қўйиш";
                default -> "Перерегистрация";
            };
        }
    },
    A_PAY {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "A-Pay orqali to'lash";
                case uz_cyrillic -> "A-Pay орқали тўлаш";
                default -> "Оплата через A-Pay";
            };
        }
    },
    WHOLESALE_TRADE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ulgurji savdo";
                case uz_cyrillic -> "Улгуржи савдо";
                default -> "Оптовая продажа";
            };
        }
    },
    FREE_REPLACEMENT_KKM {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "KKM ni bepul almashtirish (aksiya)";
                case uz_cyrillic -> "ККМ ни бепул алмаштириш (аксия)";
                default -> "Бесплатная замена ККМ (Акция)";
            };
        }
    },
    RENTAL_CDTI {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "CDTI Ijarasi";
                case uz_cyrillic -> "Ижара CDTI";
                default -> "Аренда CDTI";
            };
        }
    },
    SOFTWARE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Dasturiy ta'minot";
                case uz_cyrillic -> "Дастурий таъминот";
                default -> "Программное обеспечение";
            };
        }
    };

    private final String code;
    private final String name;

    DevicePaymentType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static DevicePaymentType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown DevicePaymentTypeEnum enum value: " + code));
    }

    @JsonCreator
    public static DevicePaymentType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for DevicePaymentTypeEnum");
    }
}
