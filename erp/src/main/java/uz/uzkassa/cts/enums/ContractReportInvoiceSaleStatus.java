package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.security.SecurityUtils;

import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ContractReportInvoiceSaleStatus {

    SOLD {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Sotilgan";
                case uz_cyrillic -> "Сотилган";
                default -> "Продан";
            };
        }
    },
    DEFECT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Kamchilik";
                case uz_cyrillic -> "Камчилик";
                default -> "Дефект";
            };
        }
    },
    DEVICE_REPLACEMENT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "NKM almashtirish";
                case uz_cyrillic -> "НКМ алмаштириш";
                default -> "Замена ККМ";
            };
        }
    },
    DEVICE_FOR_TEST {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Test uchun NKM";
                case uz_cyrillic -> "Тест учун НКМ";
                default -> "Тестовый ККМ";
            };
        }
    },
    EXCHANGE_DEVICE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Vaqtincha almashtirish uchun NKM";
                case uz_cyrillic -> "Вақтинча алмаштириш учун НКМ";
                default -> "Обменный фонд";
            };
        }
    },
    DISMANTLING {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Demontaj";
                case uz_cyrillic -> "Демонтаж";
                default -> "Разукомплектация";
            };
        }
    },
    EMPTY {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ma`lumot yo'q";
                case uz_cyrillic -> "Маълумот йўқ";
                default -> "Нет данных";
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
                case uz_latin -> "CDTI ijarasi";
                case uz_cyrillic -> "Ижара CDTI";
                default -> "Аренда CDTI";
            };
        }
    },
    FREE_DONATION {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Bepul xayriya qilingan";
                case uz_cyrillic -> "Бепул хайрия қилинган";
                default -> "Передан безвозмездно";
            };
        }
    };

    private final String code;
    private final String name;

    ContractReportInvoiceSaleStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static ContractReportInvoiceSaleStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown ContractReportInvoiceSaleStatus enum value: " + code));
    }

    @JsonCreator
    public static ContractReportInvoiceSaleStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for ContractReportInvoiceSaleStatus");
    }
}
