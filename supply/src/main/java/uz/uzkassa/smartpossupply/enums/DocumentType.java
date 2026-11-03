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
public enum DocumentType implements Serializable {
    WRITE_OFF {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Hisobdan chiqarish akti";
                case cyrillic -> "Xисобдан чиқариш акти";
                default -> "Акт списания";
            };
        }
    },
    SAMPLING {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tanlov akti";
                case cyrillic -> "Танлов акти";
                default -> "Акт отбора";
            };
        }
    },
    RETURN {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Qaytarish akti";
                case cyrillic -> "Қайтариш акти";
                default -> "Акт возврата";
            };
        }
    },
    ACCEPTANCE_AND_TRANSFER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "O'tkazish-qabul qilish akti";
                case cyrillic -> "Ўтказиш-қабул қилиш акти";
                default -> " Акт приема-передачи";
            };
        }
    },
    WITHDRAWAL {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Olib tashlash akti";
                case cyrillic -> "Олиб ташлаш акти";
                default -> "Акт изъятия";
            };
        }
    },
    PRESCRIPTION {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Retsept tayinlash";
                case cyrillic -> "Ретсепт тайинлаш";
                default -> "Рецепт, назначение";
            };
        }
    },
    DECLARATION {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yuk bojxona deklaratsiyasi";
                case cyrillic -> "Юк божхона декларацияси";
                default -> "Грузовая таможенная декларация";
            };
        }
    },
    CONTRACT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Kelishuv, shartnoma";
                case cyrillic -> "Келишув, шартнома";
                default -> "Договор, контракт";
            };
        }
    },
    STATE_CONTRACT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Hukumat shartnomasi";
                case cyrillic -> "Ҳукумат шартномаси";
                default -> "Государственный контракт";
            };
        }
    },
    POWER_OF_ATTORNEY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Vakolatnoma";
                case cyrillic -> "Ваколатнома";
                default -> "Доверенность";
            };
        }
    },
    CONSIGNMENT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yuk xati";
                case cyrillic -> "Юк хати";
                default -> "Накладная";
            };
        }
    },
    RECEIPT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Naqd pul tushumi";
                case cyrillic -> "Нақд пул тушуми";
                default -> "Кассовый чек";
            };
        }
    },
    SALES_RECEIPT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Sotish kvitansiyasi";
                case cyrillic -> "Сотиш квитансияси";
                default -> "Товарный чек";
            };
        }
    },
    OTHER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Boshqa hujjat";
                case cyrillic -> "Бошқа ҳужжат";
                default -> "Иной документ";
            };
        }
    };

    private final String name;
    private final String code;

    DocumentType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static DocumentType forValue(Map<String, String> value) {
        return DocumentType.valueOf(value.get("code"));
    }
}
