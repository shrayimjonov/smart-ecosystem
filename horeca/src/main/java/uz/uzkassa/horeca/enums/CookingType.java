package uz.uzkassa.horeca.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum CookingType implements Serializable {
    CLEANING {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tozalash";
                case cyrillic -> "Тозалаш";
                case en -> "Cleaning";
                default -> "Очистка";
            };
        }
    },
    BOILING {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Qaynatish";
                case cyrillic -> "Қайнатиш";
                case en -> "Boiling";
                default -> "Варка";
            };
        }
    },
    ROASTING {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Qovurish";
                case cyrillic -> "Қовуриш";
                case en -> "Roasting";
                default -> "Жарка";
            };
        }
    },
    EXTINGUISHING {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Тушение";
                case cyrillic -> "Тушение";
                case en -> "Extinguishing";
                default -> "Тушение";
            };
        }
    },
    BAKING {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yopish";
                case cyrillic -> "Ёпиш";
                case en -> "Baking";
                default -> "Запекание";
            };
        }
    },
    PICKLING {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Marinadlash";
                case cyrillic -> "Маринадлаш";
                case en -> "Pickling";
                default -> "Маринование";
            };
        }
    },
    MIXING {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Aralashtirish";
                case cyrillic -> "Аралаштириш";
                case en -> "Mixing";
                default -> "Смешивание";
            };
        }
    },
    SOARING {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Bug'lash";
                case cyrillic -> "Буғлаш";
                case en -> "Soaring";
                default -> "Парение";
            };
        }
    },
    RAW {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Xom";
                case cyrillic -> "Хом";
                case en -> "Raw";
                default -> "Сырье";
            };
        }
    };

    private final String name;
    private final String code;

    CookingType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }

    public static CookingType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown CookingType enum value: " + code));
    }

    @JsonCreator
    public static CookingType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for CookingType");
    }

    public static CookingType getByCode(String code) {
        return Arrays.stream(CookingType.values()).filter(departmentType -> departmentType.getCode().equals(code)).findFirst().orElse(null);
    }
}
