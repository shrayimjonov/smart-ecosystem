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
 * Date: 07.02.2024
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
@Getter
public enum GenericSettingsType implements Serializable {

    ONE_C_SYNC_ENABLED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Kirimlarni API (1C) boʻyicha sinxronlash";
                case cyrillic -> "Киримларни API (1С) бўйича синхронлаш";
                default -> "Синхронизация приходов по API (1С)";
            };
        }
    },
    ALLOW_INVOICE_SYNC {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Elektron hujjatlarni sinxronlash";
                case cyrillic -> "Электрон ҳужжатларни синхронлаш";
                default -> "Синхронизация электронных документов";
            };
        }
    },
    HAS_CONTRACT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Shartnoma";
                case cyrillic -> "Шартнома";
                default -> "Контракт";
            };
        }
    },
    IN_MARKETPLACE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Marketpleys";
                case cyrillic -> "Маркетплейс";
                default -> "Маркетплейс";
            };
        }
    },
    ONE_C_ENABLED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "1C yoqilgan";
                case cyrillic -> "1C ёқилган";
                default -> "1С включен";
            };
        }
    },
    XTRACE_ENABLED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Xtrace yoqilgan";
                case cyrillic -> "Xtrace ёқилган";
                default -> "Xtrace включен";
            };
        }
    },
    SORTING_PRODUCTS {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Mahsulotlar sortirovkasi";
                case cyrillic -> "Маҳсулотлар сортировкаси";
                default -> "Сортировка товаров";
            };
        }
    },
    WORK_WITH_AGENT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Agentlik tarmogʻi";
                case cyrillic -> "Агентлик тармоғи";
                default -> "Агентская сеть";
            };
        }
    },
    WORK_WITH_MARKS {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Markirovka";
                case cyrillic -> "Маркировка";
                default -> "Маркировка";
            };
        }
    },
    WORK_WITH_EXPEDITOR {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Ekspeditor bilan ishlash";
                case cyrillic -> "Экспедитор билан ишлаш";
                default -> " Работа с экспедитором";
            };
        }
    },
    WORK_WITH_CASHIER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Kassir bilan ishlash";
                case cyrillic -> "Кассир билан ишлаш";
                default -> " Работа с кассиром";
            };
        }
    },
    WORK_WITH_RESERVATION {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Agentlar buyurtmalari boʻyicha qoldiqlarni band qilish";
                case cyrillic -> "Агентлар буюртмалари бўйича қолдиқларни банд қилиш";
                default -> "Бронирование остатков по заказам от агентов";
            };
        }
    },
    CUSTOMER_SETTINGS {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Mijozlarni brend bo'yicha ko'rsatish";
                case cyrillic -> "Мижозларни бренд бўйича кўрсатиш";
                default -> "Отображение клиентов по бренду";
            };
        }
    },
    E_DOCUMENTS {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Elektron hujjatlar";
                case cyrillic -> "Электрон ҳужжатлар";
                default -> "Электронные документы";
            };
        }
    },
    APPROVE_ORDER_BY_AGENT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Buyurtmalarni agent tomonidan tasdiqlash";
                case cyrillic -> "Буюртмаларни агент томонидан тасдиқлаш";
                default -> "Одобрение заказов агентом";
            };
        }
    },
    CURRENCY_TYPE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Valyuta turi";
                case cyrillic -> "Валюта тури";
                default -> "Тип валюты";
            };
        }
    },
    ONLINE_KKM {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "KKM xizmat ko‘rsatish";
                case cyrillic -> "ККМ хизмат кўрсатиш";
                default -> "Обслуживание ККМ";
            };
        }
    },
    ROLE_SUPERVISOR {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Supervisor";
                case cyrillic -> "Супервайзер";
                default -> "Супервайзер";
            };
        }
    },
    EXPIRATION_DATE_CONTROL {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yaroqlilik muddatini nazorat qilish";
                case cyrillic -> "Яроқлилик муддатини назорат қилиш";
                default -> "Контроль срока годности";
            };
        }
    };

    private final String name;
    private final String code;

    GenericSettingsType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static GenericSettingsType forValue(Map<String, String> value) {
        return GenericSettingsType.valueOf(value.get("code"));
    }
}
