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
public enum LeadState implements Serializable {

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
    NO_QUESTIONS {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Savollar yo'q";
                case uz_cyrillic -> "Саволлар ёқ";
                default -> "Вопросов нет";
            };
        }
    },
    ISSUE_NOT_RESOLVED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Muammo hal etilmadi";
                case uz_cyrillic -> "Муаммо ҳал этилмади";
                default -> "Вопрос не решен";
            };
        }
    },
    NEED_CALL_BACK {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Qayta qo'ng'iroq qilish kerak";
                case uz_cyrillic -> "Қайта қўнғироқ қилиш керак";
                default -> "Нужно перезвонить";
            };
        }
    },
    OTHER {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Boshqa";
                case uz_cyrillic -> "Бошка";
                default -> "Другое";
            };
        }
    };

    private final String name;
    private final String code;

    LeadState() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static LeadState get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown Customer request status enum value: " + code));
    }

    @JsonCreator
    public static LeadState fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for Customer request status");
    }
}
