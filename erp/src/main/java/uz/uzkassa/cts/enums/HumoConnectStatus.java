package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.security.SecurityUtils;

import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum HumoConnectStatus {
    READY_TO_CONNECT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ulanishga tayyor";
                case uz_cyrillic -> "Уланишга тайёр";
                default -> "Готов к подключению";
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
    DEREGISTERED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Roʻyxatdan chiqarilgan";
                case uz_cyrillic -> "Рўйхатдан чиқарилган";
                default -> "Снят с регистрации";
            };
        }
    },
    CONNECTED {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ulangan";
                case uz_cyrillic -> "Уланган";
                default -> "Подключен";
            };
        }
    },
    PENDING {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ko`rilmoqda";
                case uz_cyrillic -> "Кўрилмоқда";
                default -> "В ожидании";
            };
        }
    },
    MISTAKE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Xatolik bilan qo'shilgan";
                case uz_cyrillic -> "Хатолик билан  қўшилган";
                default -> "Ошибочно добавлен";
            };
        }
    },
    DEFAULT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Birlamchi";
                case uz_cyrillic -> "Бирламчи";
                default -> "По умолчанию";
            };
        }
    };

    private final String code;
    private final String name;

    HumoConnectStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public static HumoConnectStatus fromOrdinal(int ordinal) {
        HumoConnectStatus[] values = HumoConnectStatus.values();
        if (ordinal < 0 || ordinal >= values.length) {
            throw new IllegalArgumentException("Invalid ordinal: " + ordinal);
        }
        return values[ordinal];
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static HumoConnectStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown HumoConnectStatusEnum enum value: " + code));
    }

    @JsonCreator
    public static HumoConnectStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for HumoConnectStatusEnum");
    }
}
