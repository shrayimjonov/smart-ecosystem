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
public enum OrderReason implements Serializable {
    ITEMS_UNAVAILABLE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Mahsulotlar mavjud emas";
                case cyrillic -> "Маҳсулотлар мавжуд эмас";
                case en -> "Items unavailable";
                default -> "Товары недоступны";
            };
        }
    },
    CUSTOMER_CANCELED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Xaridor bekor qildi";
                case cyrillic -> "Харидор бекор қилди";
                case en -> "Customer canceled";
                default -> "Клиент отменил";
            };
        }
    },
    TOO_MANY_ORDERS {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Oshxonada buyurtmalar juda ko'p";
                case cyrillic -> "Ошхонада буюртмалар жуда кўп";
                case en -> "Store has too many orders";
                default -> "На кухне слишком много заказов";
            };
        }
    },
    TECHNICAL_ISSUE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Texnik muammolar";
                case cyrillic -> "Техник муаммолар";
                case en -> "Technical issue";
                default -> "Технические проблемы";
            };
        }
    },
    OTHER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Boshqa";
                case cyrillic -> "Бошқа";
                case en -> "Other";
                default -> "Другой";
            };
        }
    };

    private final String name;

    private final String code;

    OrderReason() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }


    public static OrderReason get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown OrderReason enum value: " + code));
    }

    @JsonCreator
    public static OrderReason fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for OrderReason");
    }
}
