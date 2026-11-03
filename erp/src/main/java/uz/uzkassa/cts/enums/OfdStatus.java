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
public enum OfdStatus implements Serializable {

    ACTIVE(0) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Faol";
                case uz_cyrillic -> "Фаол";
                default -> "Активный";
            };
        }
    },
    NEW(1) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Yangi";
                case uz_cyrillic -> "Янги";
                default -> "Новый";
            };
        }
    },
    PENDING(2) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ko'rib chiqilmoqda";
                case uz_cyrillic -> "Кўриб чиқилмоқда";
                default -> "В ожидании";
            };
        }
    },
    ACCEPT(3) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Qabul qilingan";
                case uz_cyrillic -> "Қабул қилинган";
                default -> "Принятый";
            };
        }
    },
    REJECT(4) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Rad etilgan";
                case uz_cyrillic -> "Рад этилган";
                default -> "Отклонен";
            };
        }
    },
    NOT_SEND(5) {
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
    private final Integer id;

    OfdStatus(Integer id) {
        this.code = this.name();
        this.name = this.getName();
        this.id = id;
    }

    public static OfdStatus getById(Integer id) {
        if (id == null) {
            return null;
        }
        for (OfdStatus ofdStatus : OfdStatus.values()) {
            if (id.equals(ofdStatus.getId())) {
                return ofdStatus;
            }
        }
        return null;
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static OfdStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown OfdStatusEnum enum value: " + code));
    }

    @JsonCreator
    public static OfdStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for OfdStatusEnum");
    }
}
