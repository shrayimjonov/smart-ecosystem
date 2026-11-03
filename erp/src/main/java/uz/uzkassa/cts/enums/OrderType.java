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
public enum OrderType {

    LEASING {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Bo'lib to'lash";
                case uz_cyrillic -> "Бўлиб тўлаш";
                default -> "Рассрочка";
            };
        }
    },
    RENT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ijara";
                case uz_cyrillic -> "Ижара";
                default -> "Аренда";
            };
        }
    },
    SALE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Sotuv";
                case uz_cyrillic -> "Сотув";
                default -> "Продажа";
            };
        }
    },
    SERVICE {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Xizmat ko'rsatish";
                case uz_cyrillic -> "Хизмат кўрсатиш";
                default -> "Обслуживание";
            };
        }
    },
    TRADE_IN {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Treyd in";
                case uz_cyrillic -> "Трейд ин";
                default -> "Трейд ин";
            };
        }
    };

    private final String code;
    private final String name;

    OrderType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static OrderType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown OrderType enum value: " + code));
    }

    @JsonCreator
    public static OrderType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for OrderType");
    }
}
