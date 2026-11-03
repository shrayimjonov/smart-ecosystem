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
public enum DeviceStatus implements Serializable {

    REGISTERED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ro'yxatga Olingan";
                case uz_cyrillic -> "Рўйхатга Олинган";
                default -> "Зарегистрированный";
            };
        }
    },
    NOT_REGISTERED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ro'yxatga Olinmagan";
                case uz_cyrillic -> "Рўйхатга Олинмаган";
                default -> "Не Зарегистрирован";
            };
        }
    },
    UNREGISTERED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ro'yxatdan Chiqarilgan";
                case uz_cyrillic -> "Рўйхатдан Чикарилган";
                default -> "Снять с регистрации";
            };
        }
    },
    TRANSFERRED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Yuborilgan";
                case uz_cyrillic -> "Юборилган";
                default -> "Передано";
            };
        }
    },
    FISCAL {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Fiskalli va faol emas";
                case uz_cyrillic -> "Фискалли ва фаол эмас";
                default -> "Фискальный и Неактив";
            };
        }
    },
    FISCAL_ACTIVE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Fiskalli va faol";
                case uz_cyrillic -> "Фискалли ва фаол";
                default -> "Фискальный и Актив";
            };
        }
    },
    SOLD {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Sotilgan";
                case uz_cyrillic -> "Сотилган";
                default -> "Продан";
            };
        }
    },
    BRON {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Band";
                case uz_cyrillic -> "Банд";
                default -> "Брон";
            };
        }
    },
    BLOCKED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Bloklangan";
                case uz_cyrillic -> "Блокланган";
                default -> "Блокированный";
            };
        }
    },
    UN_BLOCKED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Blokdan Chiqarilgan";
                case uz_cyrillic -> "Блокдан Чиқарилган";
                default -> "Разблокирован";
            };
        }
    },
    DELETED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "O'chirilgan";
                case uz_cyrillic -> "Ўчирилган";
                default -> "Удален";
            };
        }
    },
    WRONG_ADDED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Noto'g'ri qo'shilgan";
                case uz_cyrillic -> "Нотўғри қўшилган";
                default -> "Неправильно добавлено";
            };
        }
    },
    BURNT_OUT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Kuygan";
                case uz_cyrillic -> "Куйган";
                default -> "Сгорел";
            };
        }
    },
    BROKEN {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Singan";
                case uz_cyrillic -> "Синган";
                default -> "Сломаны";
            };
        }
    },
    LOST {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Yo'qolgan";
                case uz_cyrillic -> "Йўқолган";
                default -> "Потерял";
            };
        }
    },
    STOLEN {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "O'g'rilangan";
                case uz_cyrillic -> "Ўғриланган";
                default -> "Украден";
            };
        }
    },
    OTHER {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Boshqa";
                case uz_cyrillic -> "Бошқа";
                default -> "Другой";
            };
        }
    },
    DIS_FISCAL {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "DIS_FISCAL";
                case uz_cyrillic -> "DIS_FISCAL";
                default -> "DIS_FISCAL";
            };
        }
    };

    private final String code;
    private final String name;

    DeviceStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static DeviceStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown DeviceStatusEnum enum value: " + code));
    }

    @JsonCreator
    public static DeviceStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for DeviceStatusEnum");
    }

    public static DeviceStatus getByReason(Reason reason) {
        if (reason == null) {
            return UNREGISTERED;
        }
        return switch (reason) {
            case BURNT_OUT -> BURNT_OUT;
            case BROKEN -> BROKEN;
            case LOST -> LOST;
            case WRONG_ADDED -> NOT_REGISTERED;
            default -> UNREGISTERED;
        };
    }
}
