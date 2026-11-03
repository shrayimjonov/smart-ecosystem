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
public enum ConstantReplacementReason implements Serializable {

    PURCHASE_NEW_DEVICE_INSTALLMENT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Shartnoma asosida yangi ККМ sotib olish.(Bo`lib to`lash)";
                case uz_cyrillic -> "Шартнома асосида янги ККМ сотиб олиш.(Бўлиб тўлаш)";
                default -> "Приобрёл новый ККМ по договору.(Рассрочка)";
            };
        }
    },
    PURCHASE_NEW_DEVICE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Shartnoma asosida yangi ККМ sotib olish.";
                case uz_cyrillic -> "Шартнома асосида янги ККМ сотиб олиш.";
                default -> "Приобрёл новый ККМ по договору.";
            };
        }
    },
    RESERVE_DEVICE_CUSTOMER {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Mijozning zaxira ККМ.";
                case uz_cyrillic -> "Мижознинг захира ККМ.";
                default -> "Резервный ККМ клиента.";
            };
        }
    },
    BUY_DEVICE_OTHER_COMPANY {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Qurilmani boshqa kompaniyadan sotib olish.";
                case uz_cyrillic -> "Қурилмани бошқа компаниядан сотиб олиш.";
                default -> "Перекупил ККМ у другой фирмы.";
            };
        }
    },
    PURCHASE_NEW_DEVICE_TRADE_IN {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Shartnoma asosida yangi ККМ sotib olish.(Savdo)";
                case uz_cyrillic -> "Шартнома асосида янги ККМ сотиб олиш.(Савдо)";
                default -> "Приобрёл новый ККМ по договору.(Трейд ин)";
            };
        }
    },
    DEVICE_WARRANTY_REPLACEMENT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "ККМ ni kafolatli almashtirish.(Qo'shimcha ma'lumotni qo'lda)";
                case uz_cyrillic -> "ККМ ни кафолатли алмаштириш.(Қўшимча маълумотни қўлда)";
                default -> "Гарантийная замена ККМ.(дополнительные сведения в ручную)";
            };
        }
    },
    OTHER {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Boshqa";
                case uz_cyrillic -> "Бошқа";
                default -> "Другой";
            };
        }
    };

    private final String code;
    private final String name;

    ConstantReplacementReason() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static ConstantReplacementReason get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown ConstantReplacementReasonEnum enum value: " + code));
    }

    @JsonCreator
    public static ConstantReplacementReason fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for ConstantReplacementReasonEnum");
    }
}
