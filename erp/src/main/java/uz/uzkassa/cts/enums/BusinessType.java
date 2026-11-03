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
public enum BusinessType implements Serializable {

    OOO {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "MCHJ";
                case uz_cyrillic -> "МЧЖ";
                default -> "ООО";
            };
        }
    },
    CHP {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "XK";
                case uz_cyrillic -> "ХК";
                default -> "ЧП";
            };
        }
    },
    IP {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "YaTT";
                case uz_cyrillic -> "ЯТТ";
                default -> "ИП";
            };
        }
    },
    AO {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "AJ";
                case uz_cyrillic -> "АЖ";
                default -> "АО";
            };
        }
    },
    ZAO {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "YAJ";
                case uz_cyrillic -> "ЙАЖ";
                default -> "ЗАО";
            };
        }
    },
    SP {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "QK";
                case uz_cyrillic -> "ҚК";
                default -> "СП";
            };
        }
    },
    DP {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "SHK";
                case uz_cyrillic -> "ШК";
                default -> "ДП";
            };
        }
    },
    DX {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "DX";
                case uz_cyrillic -> "ДХ";
                default -> "ДХ";
            };
        }
    },
    FX {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "FX";
                case uz_cyrillic -> "ФХ";
                default -> "ФХ";
            };
        }
    },
    UP {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "UK";
                case uz_cyrillic -> "УК";
                default -> "УП";
            };
        }
    },
    NOU {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "NTM";
                case uz_cyrillic -> "НТМ";
                default -> "НОУ";
            };
        }
    },
    CHMP {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "KTXF";
                case uz_cyrillic -> "КТХФ";
                default -> "ЧМП";
            };
        }
    },
    PK {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "DM";
                case uz_cyrillic -> "ДМ";
                default -> "ПК";
            };
        }
    },
    GUP {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "DUK";
                case uz_cyrillic -> "ДУК";
                default -> "ГУП";
            };
        }
    },
    TCHSJ {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "UJMCH";
                case uz_cyrillic -> "УЖМЧ";
                default -> "ТЧСЖ";
            };
        }
    },
    SP_OOO {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "MCHJ QK";
                case uz_cyrillic -> "МЧЖ ҚК";
                default -> "СП OOO";
            };
        }
    },
    GU {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "DK";
                case uz_cyrillic -> "ДК";
                default -> "ГУ";
            };
        }
    },
    IP_OOO {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "MCHJ XK";
                case uz_cyrillic -> "МЧЖ ХК";
                default -> "ИП ООО";
            };
        }
    },
    DOU {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "MTM";
                case uz_cyrillic -> "МТМ";
                default -> "ДОУ";
            };
        }
    },
    RG {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "RT";
                case uz_cyrillic -> "РТ";
                default -> "РГ";
            };
        }
    },
    NNT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "NNT";
                case uz_cyrillic -> "ННТ";
                default -> "ННО";
            };
        }
    },
    ODO {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "OAJ";
                case uz_cyrillic -> "ОАЖ";
                default -> "ОДО";
            };
        }
    },
    COMMUNITY_ASSOCIATION {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Jamoat birlashmasi";
                case uz_cyrillic -> "Жамоат бирлашмаси";
                default -> "Общественная ассоциация";
            };
        }
    },
    SELF_EMPLOYED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Oʻz-oʻzini band qilgan shaxs";
                case uz_cyrillic -> "Ўзини банд қилган";
                default -> "Самозанятость";
            };
        }
    };

    private final String code;
    private final String name;

    BusinessType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static BusinessType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElse(null);
    }

    @JsonCreator
    public static BusinessType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for BusinessTypeEnum");
    }
}
