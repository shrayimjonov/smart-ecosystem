package uz.uzkassa.horeca.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.horeca.utils.HeaderUtils;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum OrderStatus implements Serializable {
    BOOKED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Oldindan buyurtma";
                case cyrillic -> "Олдиндан буюртма";
                case en -> "Pre ordered";
                default -> "Предзаказано";
            };
        }
    },
    PENDING {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Kutilmoqda";
                case cyrillic -> "Кутилмоқда";
                case en -> "Pending";
                default -> "В ожидании";
            };
        }
    }, NEW {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yangi";
                case cyrillic -> "Янги";
                case en -> "New";
                default -> "Новый";
            };
        }
    },
    ACTIVE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Faol";
                case cyrillic -> "Фаол";
                case en -> "Active";
                default -> "Активный";
            };
        }
    },
    READY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tayyor";
                case cyrillic -> "Тайёр";
                case en -> "Ready";
                default -> "Готовый";
            };
        }
    },
    CANCELLED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Bekor qilingan";
                case cyrillic -> "Бекор қилинган";
                case en -> "Cancelled";
                default -> "Отменено";
            };
        }
    },
    CLOSED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yopilgan";
                case cyrillic -> "Ёпилган";
                case en -> "Closed";
                default -> "Закрыто";
            };
        }
    };

    private final String name;

    private final String code;

    OrderStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }

    public static OrderStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown OrderStatus enum value: " + code));
    }

    @JsonCreator
    public static OrderStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for OrderStatus");
    }
    public static OrderStatus getByCode(String code) {
        return Arrays.stream(OrderStatus.values()).filter(status -> status.getCode().equals(code)).findFirst().orElse(null);
    }
}
