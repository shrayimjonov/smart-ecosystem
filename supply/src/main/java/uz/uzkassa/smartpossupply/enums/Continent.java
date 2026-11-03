package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.io.Serializable;
import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum Continent implements Serializable {

    AFRICA {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Afrika";
                case cyrillic -> "Африка";
                default -> "Африка";
            };
        }
    },
    ANTARCTICA {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Antarktida";
                case cyrillic -> "Антарктида";
                default -> "Антарктида";
            };
        }
    },
    ASIA {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Osiyo";
                case cyrillic -> "Осиё";
                default -> "Азия";
            };
        }
    },
    EUROPE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yevropa";
                case cyrillic -> "Европа";
                default -> "Европа";
            };
        }
    },
    NORTH_AMERICA {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Shimoliy Amerika";
                case cyrillic -> "Шимолий Америка";
                default -> "Северная Америка";
            };
        }
    },
    SOUTH_AMERICA {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Janubiy Amerika";
                case cyrillic -> "Жанубий Америка";
                default -> "Южная Америка";
            };
        }
    },
    AUSTRALIA {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Avstraliya";
                case cyrillic -> "Австралия";
                default -> "Австралия";
            };
        }
    };

    private final String name;
    private final String code;

    Continent() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(LanguageEnum.ru);
    }

    public static Continent getByCode(String code) {
        for (Continent continent : values()) {
            if (continent.getCode().equals(code)) {
                return continent;
            }
        }
        return null;
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static Continent forValue(Map<String, String> value) {
        return Continent.valueOf(value.get("code"));
    }
}
