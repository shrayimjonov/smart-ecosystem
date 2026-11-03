package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum Month implements Serializable {

    JANUARY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yanvar";
                case cyrillic -> "Январь";
                default -> "Январь";
            };
        }
    },
    FEBRUARY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Fevral";
                case cyrillic -> "Февраль";
                default -> "Февраль";
            };
        }
    },
    MARCH {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Mart";
                case cyrillic -> "Март";
                default -> "Март";
            };
        }
    },
    APRIL {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Aprel";
                case cyrillic -> "Апрель";
                default -> "Апрель";
            };
        }
    },
    MAY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "May";
                case cyrillic -> "Май";
                default -> "Май";
            };
        }
    },
    JUNE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Iyun";
                case cyrillic -> "Июн";
                default -> "Июнь";
            };
        }
    },
    JULY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Iyul";
                case cyrillic -> "Июл";
                default -> "Июль";
            };
        }
    },
    AUGUST {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Avgust";
                case cyrillic -> "Август";
                default -> "Август";
            };
        }
    },
    SEPTEMBER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Sentyabr";
                case cyrillic -> "Сентябрь";
                default -> "Сентябрь";
            };
        }
    },
    OCTOBER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Oktyabr";
                case cyrillic -> "Октябрь";
                default -> "Октябрь";
            };
        }
    },
    NOVEMBER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Noyabr";
                case cyrillic -> "Ноябрь";
                default -> "Ноябрь";
            };
        }
    },
    DECEMBER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Dekabr";
                case cyrillic -> "Декабрь";
                default -> "Декабрь";
            };
        }
    };


    private final String name;
    private final String code;

    Month() {
        this.code = this.name();
        this.name = this.getName();
    }


    @JsonCreator
    public static Month fromValue(String value) {
        for (Month month : values()) {
            if (month.name().equalsIgnoreCase(value)) {
                return month;
            }
        }
        return null;
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }
}
