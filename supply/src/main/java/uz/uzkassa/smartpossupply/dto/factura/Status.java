package uz.uzkassa.smartpossupply.dto.factura;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import uz.uzkassa.smartpossupply.enums.LanguageEnum;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;


@Getter
public enum Status {
    DRAFT("Сохранено", "sent") {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "Saqlangan";
                case cyrillic:
                    return "Сақланган";
                default:
                    return "Сохраненные";
            }
        }
    },
    PENDING("Ожидает подписи", "sent") {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "Imzolash kutilmoqda";
                case cyrillic:
                    return "Имзолаш кутилмоқда";
                default:
                    return "Ожидающие подписи";
            }
        }
    },
    CANCELLED("Отменено", "cancel") {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "Bekor qilingan";
                case cyrillic:
                    return "Бекор қилинган";
                default:
                    return "Отмененные";
            }
        }
    },
    AGENT_ACCEPTED("Подписано агентом", "sent") {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "Agent tomonidan imzolangan";
                case cyrillic:
                    return "Агент томонидан имзоланган";
                default:
                    return "Подписанные агентом";
            }
        }
    },
    AGENT_REJECTED("Отклонено агентом", "sent") {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "Agent tomonidan rad etilgan";
                case cyrillic:
                    return "Агент томонидан рад этилган";
                default:
                    return "Отклоненные агентом";
            }
        }
    },

    ACCEPTED("Подтверждено", "accept") {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "Tasdiqlangan";
                case cyrillic:
                    return "Tasdiqlangan";
                default:
                    return "Подтвержденные";
            }
        }
    },

    REJECTED("Отклонено", "reject") {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "Rad etilgan";
                case cyrillic:
                    return "Рад этилган";
                default:
                    return "Отклоненные";
            }
        }
    },

    INVALID("Не действительно", "revoked") {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "Yaroqsiz";
                case cyrillic:
                    return "Яроқсиз";
                default:
                    return "Не действительный";
            }
        }
    },

    VALID("Действительно", "valid") {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "Yaroqli";
                case cyrillic:
                    return "Яроқли";
                default:
                    return "Действительный";
            }
        }
    };

    private final String name;
    private final String shortName;
    private final String nicStatus;

    Status(String shortName, String nicStatus) {
        this.name = getName();
        this.shortName = shortName;
        this.nicStatus = nicStatus;
    }

    public static Status fromString(String code) {
        if (code != null) {
            for (Status item : Status.values()) {
                if (code.equalsIgnoreCase(item.getName()) || code.equals(item.name())) {
                    return item;
                }
            }
        }
        return null;
    }

    @JsonCreator
    public static Status forValue(String value) {
        return fromString(value);
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }

    @Override
    public String toString() {
        return this.name();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public String getFacturaStatus() {
        return switch (this) {
            case PENDING -> "sent";
            case ACCEPTED -> "accept";
            case REJECTED -> "reject";
            case CANCELLED -> "cancel";
            default -> null;
        };
    }
}
