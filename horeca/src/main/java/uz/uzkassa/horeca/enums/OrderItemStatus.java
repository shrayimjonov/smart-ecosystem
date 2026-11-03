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
public enum OrderItemStatus implements Serializable {
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
    },
    NEW {
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
    PREPARING {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tayyorlanmoqda";
                case cyrillic -> "Тайёрланмоқда";
                case en -> "Preparing";
                default -> "Готовится";
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
    GIVEN {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Berildi";
                case cyrillic -> "Берилди";
                case en -> "Given";
                default -> "Выдан";
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
    };

    private final String name;
    private final String code;

    OrderItemStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }

    public static OrderItemStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown OrderItemStatus enum value: " + code));
    }

    @JsonCreator
    public static OrderItemStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for OrderItemStatus");
    }

    public static OrderItemStatus getByCode(String code) {
        return Arrays.stream(OrderItemStatus.values()).filter(status -> status.getCode().equals(code)).findFirst().orElse(null);
    }
}
