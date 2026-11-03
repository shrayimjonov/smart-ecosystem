package uz.uzkassa.smartpos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import uz.uzkassa.smartpos.integration.enums.LanguageEnum;
import uz.uzkassa.smartpos.utils.LanguageUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AdjustmentReason implements Serializable {
    RETAIL {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Chakana savdo";
                case cyrillic -> "Чакана савдо";
                default -> "Розничная реализация";
            };
        }
    },
    RETURN {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Jismoniy shaxsga qaytish (kommitent)";
                case cyrillic -> "Жисмоний шахсга қайтиш (коммитент)";
                default -> "Возврат физическому лицу (комитенту)";
            };
        }
    },
    REMOTE_SALE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Masofadan sotish";
                case cyrillic -> "Масофадан сотиш";
                default -> "Дистанционная продажа";
            };
        }
    },
    DESTRUCTION {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tovarlarni yo'q qilish";
                case cyrillic -> "Товарларни ёъқ қилиш";
                default -> "Уничтожение товаров";
            };
        }
    },
    CONFISCATION {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tovarlarni musodara qilish";
                case cyrillic -> "Товарларни мусодара қилиш";
                default -> "Конфискация товаров";
            };
        }
    },
    LIQUIDATION {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Kompaniyaning tugatilishi";
                case cyrillic -> "Компаниянинг тугатилиши";
                default -> "Ликвидация предприятия";
            };
        }
    },
    DONATION {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Bepul transfer";
                case cyrillic -> "Бепул трансфер";
                default -> "Безвозмездная передача";
            };
        }
    },
    STATE_ENTERPRISE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Davlat organlari va tashkilotlariga amalga oshirish";
                case cyrillic -> "Давлат органлари ва ташкилотларига амалга ошириш";
                default -> "Реализация государственным органам и организациям";
            };
        }
    },
    NO_RETAIL_USE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Xaridorning o'zi foydalanishi";
                case cyrillic -> "Харидорнинг ўзи фойдаланиши";
                default -> "Использование для собственных нужд покупателем";
            };
        }
    },
    ENTERPRISE_USE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Korxona tomonidan o'z ehtiyojlari uchun foydalanish";
                case cyrillic -> "Корхона томонидан ўз эҳтиёжлари учун фойдаланиш";
                default -> "Использование для собственных нужд предприятием";
            };
        }
    },
    MEDICAL_PURPOSE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tibbiy foydalanish";
                case cyrillic -> "Тиббий фойдаланиш";
                default -> "Использование по медицинскому назначению";
            };
        }
    },
    FREE_PRESCRIPTION {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Bepul retsept";
                case cyrillic -> "Тиббий фойдаланиш";
                default -> "Отпуск по бесплатному рецепту";
            };
        }
    },
    DEFECT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Nuqsonli";
                case cyrillic -> "Нуқсонли";
                default -> "Брак";
            };
        }
    },
    EXPIRY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Muddati tugagan";
                case cyrillic -> "Муддати тугаган";
                default -> "Истек срок годности";
            };
        }
    },
    PRODUCT_RECALL {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Bozordan fikr-mulohazalar";
                case cyrillic -> "Бозордан фикр-мулоҳазалар";
                default -> "Отзыв с рынка";
            };
        }
    },
    COMPLAINTS {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Reklama";
                case cyrillic -> "Реклама";
                default -> "Рекламации";
            };
        }
    },
    DEMO_SAMPLES {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Namoyish namunalari";
                case cyrillic -> "Намойиш намуналари";
                default -> "Демонстрационные образцы";
            };
        }
    },
    DAMAGE_LOSS {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tovarlarni yo'qotish";
                case cyrillic -> "Товарларни ёъқотиш";
                default -> "Утрата товаров";
            };
        }
    },
    PROCESSING {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Ishlab chiqarish ehtiyojlari uchun foydalanish (qayta ishlash)";
                case cyrillic -> "Ишлаб чиқариш эҳтиёжлари учун фойдаланиш (қайта ишлаш)";
                default -> "Использование для производственных нужд (переработка)";
            };
        }
    },
    EXPORT {
        @Override
        public String getName(LanguageEnum language) {
            if (Objects.requireNonNull(language) == LanguageEnum.uz) {
                return "Eksport";
            }
            return "Экспорт";
        }
    },
    OTHER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Boshqa sabablar";
                case cyrillic -> "Бошқа сабаблар";
                default -> "Другие причины";
            };
        }
    };

    private final String name;
    private final String code;

    AdjustmentReason() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(LanguageUtils.getLanguage());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static AdjustmentReason forValue(Map<String, String> value) {
        return AdjustmentReason.valueOf(value.get("code"));
    }
}
