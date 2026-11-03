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
public enum Reason implements Serializable {

    WRONG_ADDED(null) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Noto'g'ri qo'shilgan";
                case uz_cyrillic -> "Нотўғри қўшилган";
                default -> "Неправильно добавлено";
            };
        }
    },
    BURNT_OUT(2) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Kuygan";
                case uz_cyrillic -> "Куйган";
                default -> "Сгорел";
            };
        }
    },
    BROKEN(2) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Singan";
                case uz_cyrillic -> "Синган";
                default -> "Сломанный";
            };
        }
    },
    MEMORY_IS_FULL(1) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Xotira to'lgan";
                case uz_cyrillic -> "Хотира тўлган";
                default -> "Память заполнена";
            };
        }
    },
    LOST(4) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Yo'qolgan";
                case uz_cyrillic -> "Йўқолган";
                default -> "Потерял";
            };
        }
    },
    TEMPORARY_REPLACEMENT(null) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Vaqtinchalik almashtirish";
                case uz_cyrillic -> "Вақтинчалик алмаштириш";
                default -> "Временная замена";
            };
        }
    },
    REVERSE_SUBSTITUTION(null) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Qayta almashtirish";
                case uz_cyrillic -> "Қайта алмаштириш";
                default -> "Обратная замена";
            };
        }
    },
    OTHER(2) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Boshqa";
                case uz_cyrillic -> "Бошқа";
                default -> "Другой";
            };
        }
    };

    private final String code;
    private final String name;
    private final Integer number;

    Reason(Integer number) {
        this.code = this.name();
        this.name = this.getName();
        this.number = number;
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static Reason get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown ReasonEnum enum value: " + code));
    }

    @JsonCreator
    public static Reason fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for ReasonEnum");
    }
}
