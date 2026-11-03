package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.security.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum FeedBackStatus implements Serializable {

    NEW {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Yangi";
                case uz_cyrillic -> "Янги";
                default -> "Новый";
            };
        }
    },
    IN_PROGRESS {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Jarayonda";
                case uz_cyrillic -> "Жараёнда";
                default -> "В процессе";
            };
        }
    },
    WAITING_CLIENT_RESPONSE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Mijoz javobini kutmoqda";
                case uz_cyrillic -> "Мижоз жавобини кутмоқда";
                default -> "Ожидает ответа клиента";
            };
        }
    },
    WAITING_APPROVAL {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Tasdiqlash kutilmoqda";
                case uz_cyrillic -> "Тасдиқлаш кутилмоқда";
                default -> "Ожидает согласования";
            };
        }
    },
    CANCELED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Bekor qilingan";
                case uz_cyrillic -> "Бекор килинган";
                default -> "Отменено";
            };
        }
    },
    ON_CONTROL {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Nazoratda";
                case uz_cyrillic -> "Назоратда";
                default -> "На контроле";
            };
        }
    },
    CLOSED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Yopilgan";
                case uz_cyrillic -> "Ёпилган";
                default -> "Закрыта";
            };
        }
    },
    SOLVED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Hal qilindi";
                case uz_cyrillic -> "Ҳал қилинди";
                default -> "Решена";
            };
        }
    };

    private final String name;
    private final String code;

    FeedBackStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static FeedBackStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown Feedback status enum value: " + code));
    }

    @JsonCreator
    public static FeedBackStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for Feedback status");
    }
}
