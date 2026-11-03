package uz.uzkassa.smartpos.integration.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import uz.uzkassa.smartpos.utils.LanguageUtils;

import java.io.Serializable;
import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BusinessTypeEnum implements Serializable {
    OOO {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.uz) {
                return "OOO";
            }
            return "ООО";
        }
    },
    CHP {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.uz) {
                return "CHP";
            }
            return "ЧП";
        }
    },
    IP {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.uz) {
                return "IP";
            }
            return "ИП";
        }
    },
    AO {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.uz) {
                return "AO";
            }
            return "АО";
        }
    },
    ZAO {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.uz) {
                return "ZAO";
            }
            return "ЗАО";
        }
    },
    SP {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.uz) {
                return "SP";
            }
            return "СП";
        }
    },
    DP {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.uz) {
                return "DP";
            }
            return "ДП";
        }
    },
    DX {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.uz) {
                return "DX";
            }
            return "ДХ";
        }
    },
    FX {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.uz) {
                return "FX";
            }
            return "ФХ";
        }
    },
    UP {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.uz) {
                return "UP";
            }
            return "УП";
        }
    },
    NOU {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.uz) {
                return "NOU";
            }
            return "НОУ";
        }
    },

    CHMP {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.uz) {
                return "CHMP";
            }
            return "ЧМП";
        }
    },
    PK {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.uz) {
                return "PK";
            }
            return "ПК";
        }
    },
    GUP {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.uz) {
                return "GUP";
            }
            return "ГУП";
        }
    },

    TCHSJ {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.uz) {
                return "TCHSJ";
            }
            return "ТЧСЖ";
        }
    },

    SP_OOO {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.uz) {
                return "SP_OOO";
            }
            return "СП OOO";
        }
    },
    GU {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.uz) {
                return "GU";
            }
            return "ГУ";
        }
    },
    IP_OOO {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.uz) {
                return "Ip OOO";
            }
            return "ИП ООО";
        }
    },
    DUO {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Maktabgacha ta'lim muassasasi";
                case cyrillic -> "Мактабгача таълим муассасаси";
                default -> "Дошкольное образовательное учреждение";
            };
        }
    },
    RG {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Ro`znoma tahririyati";
                case cyrillic -> "Рузнома таҳририяти";
                default -> "Редакция газеты";
            };
        }
    },
    ODO {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Qo'shimcha mas'uliyatli jamiyat";
                case cyrillic -> "Қўшимча масъулиятли жамият";
                default -> "Общество с дополнительной ответственностью";
            };
        }
    };

    private final String name;
    private final String code;

    BusinessTypeEnum() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(LanguageUtils.getLanguage());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static BusinessTypeEnum forValue(Map<String, String> value) {
        return BusinessTypeEnum.valueOf(value.get("code"));
    }
}
