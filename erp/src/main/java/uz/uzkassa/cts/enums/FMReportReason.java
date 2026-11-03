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
public enum FMReportReason implements Serializable {

    BURNT_OUT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Замена ФМ (Сгорел)";
                case uz_cyrillic -> "Замена ФМ (Сгорел)";
                default -> "Замена ФМ (Сгорел)";
            };
        }
    },
    MEMORY_IS_FULL {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Замена ФМ (Память заполнена)";
                case uz_cyrillic -> "Замена ФМ (Память заполнена)";
                default -> "Замена ФМ (Память заполнена)";
            };
        }
    },
    LOST {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Замена ФМ (Потерял)";
                case uz_cyrillic -> "Замена ФМ (Потерял)";
                default -> "Замена ФМ (Потерял)";
            };
        }
    },
    FM_LOST {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Снятие с регистрации (ФМ утерян)";
                case uz_cyrillic -> "Снятие с регистрации (ФМ утерян)";
                default -> "Снятие с регистрации (ФМ утерян)";
            };
        }
    },
    FM_NOT_LOST {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Снятие с регистрации (ФМ имеется)";
                case uz_cyrillic -> "Снятие с регистрации (ФМ имеется)";
                default -> "Снятие с регистрации (ФМ имеется)";
            };
        }
    },
    NO_REASON {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Sabab yo'q";
                case uz_cyrillic -> "Сабаб йўқ";
                default -> "Нет причин";
            };
        }
    };

    private final String code;
    private final String name;

    FMReportReason() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static FMReportReason get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown FMReportReasonEnum enum value: " + code));
    }

    @JsonCreator
    public static FMReportReason fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for FMReportReasonEnum");
    }

    public static FMReportReason fromRussianToFMReportEnum(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        return switch (value.trim()) {
            case "Замена ФМ (Сгорел)" -> BURNT_OUT;
            case "Замена ФМ (Память заполнена)" -> MEMORY_IS_FULL;
            case "Замена ФМ (Потерял)" -> LOST;
            case "Снятие с регистрации (ФМ утерян)" -> FM_LOST;
            case "Снятие с регистрации (ФМ имеется)" -> FM_NOT_LOST;
            case "Нет причин" -> NO_REASON;
            default -> throw new IllegalArgumentException("Unknown Russian FMReportReason value: " + value);
        };
    }
}
