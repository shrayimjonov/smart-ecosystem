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
public enum HallItemReason implements Serializable {
    MERGE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Birlashtirish";
                case cyrillic -> "Бирлаштириш";
                case en -> "Merge";
                default -> "Объединить";
            };
        }
    },

    TECHNICAL_SERVICES {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Texnik xizmat";
                case cyrillic -> "Техник хизмат";
                case en -> "Maintenance";
                default -> "Техническое обслуживание";
            };
        }
    },

    RESERVATION_FOR_EVENT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tadbir uchun bron qilish";
                case cyrillic -> "Тадбир учун брон қилиш";
                case en -> "Reservation for an event";
                default -> "Резервация для мероприятия";
            };
        }
    },

    SANITARY_REASON {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Sanitariya sabablari";
                case cyrillic -> "Санитария сабаблари   ";
                case en -> "Sanitary reasons";
                default -> "Санитарные причины";
            };
        }
    },
    RESERVATION_FOR_SPECIAL_CLIENT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Doimiy mijoz uchun bron qilish";
                case cyrillic -> "Доимий мижоз учун брон қилиш";
                case en -> "Reservation for a special client";
                default -> "Резервация для постоянного клиента";
            };
        }
    },
    PLANNING_AND_REARRANGEMENT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Joylashtirish va tartibga solish";
                case cyrillic -> "Жойлаштириш ва тартибга солиш";
                case en -> "Planning and arrangement";
                default -> "Планировка и расстановка";
            };
        }
    },
    OTHER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Boshqa sabablar";
                case cyrillic -> "Бошқа сабаблар";
                case en -> "Other reasons";
                default -> "Другие причины";
            };
        }
    };

    private final String name;

    private final String code;

    HallItemReason() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }

    public static HallItemReason get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown HallItemReason enum value: " + code));
    }

    @JsonCreator
    public static HallItemReason fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for HallItemReason");
    }

    public static HallItemReason getByCode(String code) {
        return Arrays.stream(HallItemReason.values()).filter(reason -> reason.getCode().equals(code)).findFirst().orElse(null);
    }
}
