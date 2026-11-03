package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.security.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum DocumentType implements Serializable {

    CUSTOMER_BRANCH_LOGO {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "CUSTOMER_BRANCH_LOGO";
                case uz_cyrillic -> "CUSTOMER_BRANCH_LOGO";
                default -> "CUSTOMER_BRANCH_LOGO";
            };
        }
    },
    CUSTOMER_BRANCH_CASH_REGISTER_AREA {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "CUSTOMER_BRANCH_CASH_REGISTER_AREA";
                case uz_cyrillic -> "CUSTOMER_BRANCH_CASH_REGISTER_AREA";
                default -> "CUSTOMER_BRANCH_CASH_REGISTER_AREA";
            };
        }
    },
    CUSTOMER_BRANCH_INTERIOR {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "CUSTOMER_BRANCH_INTERIOR";
                case uz_cyrillic -> "CUSTOMER_BRANCH_INTERIOR";
                default -> "CUSTOMER_BRANCH_INTERIOR";
            };
        }
    },
    CUSTOMER_BRANCH_EXTERIOR {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "CUSTOMER_BRANCH_EXTERIOR";
                case uz_cyrillic -> "CUSTOMER_BRANCH_EXTERIOR";
                default -> "CUSTOMER_BRANCH_EXTERIOR";
            };
        }
    },
    EMPLOYEE_PROFILE_PHOTO {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "EMPLOYEE_PROFILE_PHOTO";
                case uz_cyrillic -> "EMPLOYEE_PROFILE_PHOTO";
                default -> "EMPLOYEE_PROFILE_PHOTO";
            };
        }
    },
    EMPLOYEE_TIME_TRACK_PHOTO {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "EMPLOYEE_TIME_TRACK_PHOTO";
                case uz_cyrillic -> "EMPLOYEE_TIME_TRACK_PHOTO";
                default -> "EMPLOYEE_TIME_TRACK_PHOTO";
            };
        }
    },
    BIRTH_CERTIFICATE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Tug‘ilgan haqida guvohnoma";
                case uz_cyrillic -> "Туғилган хақида гувохнома";
                default -> "Свидетельство о рождении";
            };
        }
    },
    ATTACHMENT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ilova";
                case uz_cyrillic -> "Илова";
                default -> "Прикрепление";
            };
        }
    },
    DOCUMENT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Hujjat";
                case uz_cyrillic -> "Ҳужжат";
                default -> "Документ";
            };
        }
    },
    PASSPORT_COPY {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Pasport Nusxa";
                case uz_cyrillic -> "Паспорт Нусха";
                default -> "Копия Паспорта";
            };
        }
    },
    CERTIFICATE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Guvohnoma";
                case uz_cyrillic -> "Гувоҳнома";
                default -> "Сертификат";
            };
        }
    },
    HUMO {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Humo";
                case uz_cyrillic -> "Ҳумо";
                default -> "Хумо";
            };
        }
    },
    APELSIN {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Apelsin";
                case uz_cyrillic -> "Апелсин";
                default -> "Апельсин";
            };
        }
    },
    LEASE_CONTRACT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ijara Shartnoma";
                case uz_cyrillic -> "Ижара Шартнома";
                default -> "Договор аренды";
            };
        }
    },
    CADASTRE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Kadastr";
                case uz_cyrillic -> "Кадастр";
                default -> "Кадастр";
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
    },
    ORDER_SPECIFICATION {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Spesifikatsiya";
                case uz_cyrillic -> "Спецификация";
                default -> "Спецификация";
            };
        }
    },
    SERVICE_SPECIFICATION {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Spesifikatsiya";
                case uz_cyrillic -> "Спецификация";
                default -> "Спецификация";
            };
        }
    },
    POWER_ATTORNEY {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ishonchnoma";
                case uz_cyrillic -> "Ишончнома";
                default -> "Доверенность";
            };
        }
    },
    INVOICE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Hisob faktura";
                case uz_cyrillic -> "Ҳисоб фактура";
                default -> "Счет фактура";
            };
        }
    },
    SERVICE_INVOICE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Hisob faktura";
                case uz_cyrillic -> "Ҳисоб фактура";
                default -> "Счет фактура";
            };
        }
    },
    ONE_TIME_ORDER {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Bir martalik xizmat";
                case uz_cyrillic -> "Бир марталик хизмат";
                default -> "Единоразовая услуга";
            };
        }
    },
    CONTRACT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Shartnoma";
                case uz_cyrillic -> "Шартнома";
                default -> "Договор";
            };
        }
    },
    DEVICE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "NKM hujjati";
                case uz_cyrillic -> "НКМ документ";
                default -> "ККМ документ";
            };
        }
    },
    APPLICATION {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ariza";
                case uz_cyrillic -> "Ариза";
                default -> "Заявление";
            };
        }
    },
    CONTRACT_FOR_LEASING {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Bo`lib to`lashga ariza";
                case uz_cyrillic -> "Булиб тулашга ариза";
                default -> "Заявка на рассрочку";
            };
        }
    },
    WARRANTY_FOR_LEASING {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "WARRANTY_FOR_LEASING";
                case uz_cyrillic -> "WARRANTY_FOR_LEASING";
                default -> "WARRANTY_FOR_LEASING";
            };
        }
    },
    ACT_OF_TRANSMISSION {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "ACT_OF_TRANSMISSION";
                case uz_cyrillic -> "ACT_OF_TRANSMISSION";
                default -> "ACT_OF_TRANSMISSION";
            };
        }
    },
    ACT_PAYMENT_SCHEDULE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "ACT_PAYMENT_SCHEDULE";
                case uz_cyrillic -> "ACT_PAYMENT_SCHEDULE";
                default -> "ACT_PAYMENT_SCHEDULE";
            };
        }
    },
    ACT_POWER_ATTORNEY {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "ACT_POWER_ATTORNEY";
                case uz_cyrillic -> "ACT_POWER_ATTORNEY";
                default -> "ACT_POWER_ATTORNEY";
            };
        }
    },
    LEASING_INVOICE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "LEASING_INVOICE";
                case uz_cyrillic -> "LEASING_INVOICE";
                default -> "LEASING_INVOICE";
            };
        }
    },
    UPDATE_DATA {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ma'lumotlarni o'zgartirish";
                case uz_cyrillic -> "Маълумотларни ўзгартириш";
                default -> "Обновить данные";
            };
        }
    },
    PAYMENT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "To'lov";
                case uz_cyrillic -> "Тўлов";
                default -> "Оплата";
            };
        }
    },
    TRADE_IN_CONTRACT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "TRADE_IN_CONTRACT";
                case uz_cyrillic -> "TRADE_IN_CONTRACT";
                default -> "TRADE_IN_CONTRACT";
            };
        }
    },
    TRADE_IN_ACT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "TRADE_IN_ACT";
                case uz_cyrillic -> "TRADE_IN_ACT";
                default -> "TRADE_IN_ACT";
            };
        }
    },
    TRADE_IN_POWER_ATTORNEY {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "TRADE_IN_POWER_ATTORNEY";
                case uz_cyrillic -> "TRADE_IN_POWER_ATTORNEY";
                default -> "TRADE_IN_POWER_ATTORNEY";
            };
        }
    },
    TRADE_IN_COUNTER_CONTRACT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "TRADE_IN_COUNTER_CONTRACT";
                case uz_cyrillic -> "TRADE_IN_COUNTER_CONTRACT";
                default -> "TRADE_IN_COUNTER_CONTRACT";
            };
        }
    },
    TRADE_IN_COUNTER_ACT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "TRADE_IN_COUNTER_ACT";
                case uz_cyrillic -> "TRADE_IN_COUNTER_ACT";
                default -> "TRADE_IN_COUNTER_ACT";
            };
        }
    },
    TRADE_IN_COUNTER_POWER_ATTORNEY {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "TRADE_IN_COUNTER_POWER_ATTORNEY";
                case uz_cyrillic -> "TRADE_IN_COUNTER_POWER_ATTORNEY";
                default -> "TRADE_IN_COUNTER_POWER_ATTORNEY";
            };
        }
    },
    TRADE_IN_COUNTER_CONTRACT_NOTE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "TRADE_IN_COUNTER_CONTRACT_NOTE";
                case uz_cyrillic -> "TRADE_IN_COUNTER_CONTRACT_NOTE";
                default -> "TRADE_IN_COUNTER_CONTRACT_NOTE";
            };
        }
    },
    TRADE_IN_COUNTER_CONTRACT_FILE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "TRADE_IN_COUNTER_CONTRACT_FILE";
                case uz_cyrillic -> "TRADE_IN_COUNTER_CONTRACT_FILE";
                default -> "TRADE_IN_COUNTER_CONTRACT_FILE";
            };
        }
    },
    RENT_ACT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "RENT_ACT";
                case uz_cyrillic -> "RENT_ACT";
                default -> "RENT_ACT";
            };
        }
    },
    RENT_ACT_POWER_ATTORNEY {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "RENT_ACT_POWER_ATTORNEY";
                case uz_cyrillic -> "RENT_ACT_POWER_ATTORNEY";
                default -> "RENT_ACT_POWER_ATTORNEY";
            };
        }
    },
    RENT_ACT_OF_RETURN {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "RENT_ACT_OF_RETURN";
                case uz_cyrillic -> "RENT_ACT_OF_RETURN";
                default -> "RENT_ACT_OF_RETURN";
            };
        }
    },
    RENT_ACT_OF_RETURN_POWER_ATTORNEY {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "RENT_ACT_OF_RETURN_POWER_ATTORNEY";
                case uz_cyrillic -> "RENT_ACT_OF_RETURN_POWER_ATTORNEY";
                default -> "RENT_ACT_OF_RETURN_POWER_ATTORNEY";
            };
        }
    },
    RENT_ACT_IMAGE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "RENT_ACT_IMAGE";
                case uz_cyrillic -> "RENT_ACT_IMAGE";
                default -> "RENT_ACT_IMAGE";
            };
        }
    },
    RENT_ACT_OF_RETURN_IMAGE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "RENT_ACT_OF_RETURN_IMAGE";
                case uz_cyrillic -> "RENT_ACT_OF_RETURN_IMAGE";
                default -> "RENT_ACT_OF_RETURN_IMAGE";
            };
        }
    },
    CASE_FILE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "CASE_FILE";
                case uz_cyrillic -> "CASE_FILE";
                default -> "CASE_FILE";
            };
        }
    },
    TRADE_IN_LETTER_OF_GUARANTEE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "TRADE_IN_LETTER_OF_GUARANTEE";
                case uz_cyrillic -> "TRADE_IN_LETTER_OF_GUARANTEE";
                default -> "TRADE_IN_LETTER_OF_GUARANTEE";
            };
        }
    },
    SALE_CONTRACT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "SALE_CONTRACT";
                case uz_cyrillic -> "SALE_CONTRACT";
                default -> "SALE_CONTRACT";
            };
        }
    },
    SALE_ACT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "SALE_ACT";
                case uz_cyrillic -> "SALE_ACT";
                default -> "SALE_ACT";
            };
        }
    },
    SALE_POWER_ATTORNEY {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "SALE_POWER_ATTORNEY";
                case uz_cyrillic -> "SALE_POWER_ATTORNEY";
                default -> "SALE_POWER_ATTORNEY";
            };
        }
    },
    PAYMENT_CONTRACT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "PAYMENT_CONTRACT";
                case uz_cyrillic -> "PAYMENT_CONTRACT";
                default -> "PAYMENT_CONTRACT";
            };
        }
    },
    PAYMENT_ACT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "PAYMENT_ACT";
                case uz_cyrillic -> "PAYMENT_ACT";
                default -> "PAYMENT_ACT";
            };
        }
    },
    PAYMENT_ACT_POWER_ATTORNEY {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "PAYMENT_ACT_POWER_ATTORNEY";
                case uz_cyrillic -> "PAYMENT_ACT_POWER_ATTORNEY";
                default -> "PAYMENT_ACT_POWER_ATTORNEY";
            };
        }
    },
    PAYMENT_COMPANY_CONTRACT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "PAYMENT_COMPANY_CONTRACT";
                case uz_cyrillic -> "PAYMENT_COMPANY_CONTRACT";
                default -> "PAYMENT_COMPANY_CONTRACT";
            };
        }
    },
    FISCAL_MODULE_DOC {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "FISCAL_MODULE_DOC";
                case uz_cyrillic -> "FISCAL_MODULE_DOC";
                default -> "FISCAL_MODULE_DOC";
            };
        }
    },
    CUSTOMER_BRANCH_SCANNER {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "CUSTOMER_BRANCH_SCANNER";
                case uz_cyrillic -> "CUSTOMER_BRANCH_SCANNER";
                default -> "CUSTOMER_BRANCH_SCANNER";
            };
        }
    },
    RENT_DEVICE_ACT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "RENT_DEVICE_ACT";
                case uz_cyrillic -> "RENT_DEVICE_ACT";
                default -> "RENT_DEVICE_ACT";
            };
        }
    },
    RENT_DEVICE_ACT_OF_RETURN {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "RENT_DEVICE_ACT_OF_RETURN";
                case uz_cyrillic -> "RENT_DEVICE_ACT_OF_RETURN";
                default -> "RENT_DEVICE_ACT_OF_RETURN";
            };
        }
    },
    RENT_DEVICE_ACT_POWER_ATTORNEY {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "RENT_DEVICE_ACT_POWER_ATTORNEY";
                case uz_cyrillic -> "RENT_DEVICE_ACT_POWER_ATTORNEY";
                default -> "RENT_DEVICE_ACT_POWER_ATTORNEY";
            };
        }
    },
    VACANCY_APPLICATION {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "VACANCY_APPLICATION";
                case uz_cyrillic -> "VACANCY_APPLICATION";
                default -> "VACANCY_APPLICATION";
            };
        }
    },
    INTEGRATION_LOGO {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "INTEGRATION_LOGO";
                case uz_cyrillic -> "INTEGRATION_LOGO";
                default -> "INTEGRATION_LOGO";
            };
        }
    },
    PRODUCT_PHOTO {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "PRODUCT_PHOTO";
                case uz_cyrillic -> "PRODUCT_PHOTO";
                default -> "PRODUCT_PHOTO";
            };
        }
    },
    FEEDBACK_PHOTO {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "FEEDBACK_PHOTO";
                case uz_cyrillic -> "FEEDBACK_PHOTO";
                default -> "FEEDBACK_PHOTO";
            };
        }
    },
    FEEDBACK_VIDEO {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "FEEDBACK_VIDEO";
                case uz_cyrillic -> "FEEDBACK_VIDEO";
                default -> "FEEDBACK_VIDEO";
            };
        }
    },
    NOTE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "NOTE";
                case uz_cyrillic -> "NOTE";
                default -> "NOTE";
            };
        }
    },
    CHAT_ATTACHMENT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "CHAT_ATTACHMENT";
                case uz_cyrillic -> "CHAT_ATTACHMENT";
                default -> "CHAT_ATTACHMENT";
            };
        }
    };

    private final String code;
    private final String name;

    DocumentType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static DocumentType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown DocumentType enum value: " + code));
    }

    @JsonCreator
    public static DocumentType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for DocumentType");
    }
}
