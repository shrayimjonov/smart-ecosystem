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
public enum GenericSettingsFieldType implements Serializable {

    LOGIN {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Login";
                case cyrillic -> "Логин";
                default -> "Логин";
            };
        }
    },
    PASSWORD {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Parol";
                case cyrillic -> "Пароль";
                default -> "Пароль";
            };
        }
    },
    URL {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Serverning URL manzili";
                case cyrillic -> "Сервернинг URL манзили";
                default -> "URL-адрес";
            };
        }
    },
    ENABLE_DISABLE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yoqish/O'chirish";
                case cyrillic -> "Ёқиш/Ўчириш";
                default -> "Включить/Выключить";
            };
        }
    },
    EXPIRE_DATE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tugash sanasi";
                case cyrillic -> "Тугаш санаси";
                default -> "Дата окончания";
            };
        }
    },
    GS1_CODE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "GS1 Kod";
                case cyrillic -> "GS1 Код";
                default -> "GS1 Код";
            };
        }
    },
    OMS_ID {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "OMSID";
                case cyrillic -> "OMSID";
                default -> "OMSID";
            };
        }
    },
    TURON_TOKEN {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Turon token";
                case cyrillic -> "Турон токен";
                default -> "Турон токен";
            };
        }
    },
    XTRACE_TOKEN {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Xtrace token";
                case cyrillic -> "Xtrace токен";
                default -> "Xtrace токен";
            };
        }
    },
    ALL_CUSTOMER_LIST {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Barcha mijozlar";
                case cyrillic -> "Барча мижозлар";
                default -> "Все клиенты";
            };
        }
    },
    PLAN_ROUTE_CUSTOMER_LIST {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yoʻnalish boʻyicha mijozlar";
                case cyrillic -> "Йўналиш бўйича мижозлар";
                default -> "Клиенты по маршруту";
            };
        }
    },
    ALLOWED_DISTANCE_BETWEEN_AGENT_AND_CUSTOMER_BRANCH {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Agentdan savdo nuqtasigacha bo'lgan ruxsat etilgan masofa";
                case cyrillic -> "Агентдан савдо нуқтасигача бўлган рухсат этилган масофа";
                default -> "Разрешенное расстояние от агента до торговой точки";
            };
        }
    },
    ALLOW_AGENT_TO_UPDATE_CUSTOMER_DATA {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Savdo agentlariga mijozlar ma'lumotlarini o'zgartirishga ruxsat berish";
                case cyrillic -> "Савдо агентларига мижозлар маълумотларини ўзгартиришга рухсат бериш";
                default -> "Разрешить торговым агентам изменять данные о клиенте";
            };
        }
    },
    CONTACT_PERSON {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Aloqaga chiqadigan shaxs";
                case cyrillic -> "Алоқага чиқадиган шахс";
                default -> "Контактное лицо";
            };
        }
    },
    CUSTOMERS {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Mijozlar";
                case cyrillic -> "Мижозлар";
                default -> "Клиенты";
            };
        }
    },
    GENERATE_DOCS_AUTOMATICALLY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Elektron hujjatlarni avtomatik ravishda yaratish";
                case cyrillic -> "Электрон ҳужжатларни автоматик равишда яратиш";
                default -> "Автоматически создавать электронные документы";
            };
        }
    },
    FACTURA {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Hisobvaraq-fakturalar";
                case cyrillic -> "Ҳисобварақ-фактуралар";
                default -> "Счет-фактуры";
            };
        }
    },
    WAYBILL {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tovar-transport yukxati";
                case cyrillic -> "Товар-транспорт юкхати";
                default -> "Товарно-транспортная накладная";
            };
        }
    },
    ACCEPTANCE_TRANSFER_ACT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Dalolatnomalar";
                case cyrillic -> "Далолатномалар";
                default -> "Акты";
            };
        }
    },
    PRODUCT_GROUP {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tovar guruhi";
                case cyrillic -> "Товар гуруҳи";
                default -> "Товарная группа";
            };
        }
    },
    UZS {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "So'm";
                case cyrillic -> "Сўм";
                default -> "Сом";
            };
        }
    },
    USD {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Dollar";
                case cyrillic -> "Доллар";
                default -> "Доллар";
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
    EXCHANGE_RATE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Valyuta kursi";
                case cyrillic -> "Валюта курси";
                default -> "Обменный курс";
            };
        }
    },
    NEED_CODE_SCAN {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Kodlarni qayta skanerlash";
                case cyrillic -> "Кодларни қайта сканерлаш";
                default -> "Необходимость досканирования кодов";
            };
        }
    },
    CHECK_CODES {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Oflayn rejimda ishlash";
                case cyrillic -> "Офлайн режимда ишлаш";
                default -> "Работа в оффлайн режиме";
            };
        }
    },
    ;

    private final String name;
    private final String code;

    GenericSettingsFieldType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static GenericSettingsFieldType forValue(Map<String, String> value) {
        return GenericSettingsFieldType.valueOf(value.get("code"));
    }
}
