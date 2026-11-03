package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.security.SecurityUtils;

import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum XfileStatus {
    DRAFT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Saqlangan";
                case uz_cyrillic -> "Сақланган";
                default -> "Сохраненные";
            };
        }
    },
    SENT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Yuborilgan";
                case uz_cyrillic -> "Юборилган";
                default -> "Отправленные";
            };
        }
    },
    QUEUED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Yetkazib berishda";
                case uz_cyrillic -> "Етказиб беришда";
                default -> "В отправке";
            };
        }
    },
    PENDING {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Kutilmoqda";
                case uz_cyrillic -> "Кутилмоқда";
                default -> "Ожидающие";
            };
        }
    },
    CANCELLED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Bekor qilingan";
                case uz_cyrillic -> "Бекор қилинган";
                default -> "Отмененные";
            };
        }
    },
    AGENT_ACCEPTED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Agent tomonidan imzolangan";
                case uz_cyrillic -> "Aгент томонидан имзоланган";
                default -> "Подписанные агентом";
            };
        }
    },
    AGENT_REJECTED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Agent tomonidan rad etilgan";
                case uz_cyrillic -> "Aгент томонидан рад этилган";
                default -> "Отклоненные агентом";
            };
        }
    },
    ACCEPTED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Imzolangan";
                case uz_cyrillic -> "Имзоланган";
                default -> "Подписанные";
            };
        }
    },
    REJECTED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Rad etilgan";
                case uz_cyrillic -> "Рад этилган";
                default -> "Отказанные";
            };
        }
    },
    REVOKED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "O'zgartirilgan";
                case uz_cyrillic -> "Ўзгартирилган";
                default -> "Измененные";
            };
        }
    },
    ERROR {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Yuborishda xato";
                case uz_cyrillic -> "Юборишда хато";
                default -> "Ошибка в отправлении";
            };
        }
    },
    OLD_ERROR {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Yuborishda xatolik yuz berdi OLD_ERROR";
                case uz_cyrillic -> "Юборишда хатолик юз берди OLD_ERROR";
                default -> "Ошибка в отправлении OLD_ERROR";
            };
        }
    },
    NOT_SEND {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Yuborilmagan";
                case uz_cyrillic -> "Юборилмаган";
                default -> "Не отправлено";
            };
        }
    };

    private final String code;
    private final String name;

    XfileStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static XfileStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown XfileStatus enum value: " + code));
    }

    @JsonCreator
    public static XfileStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for XfileStatus");
    }
}
