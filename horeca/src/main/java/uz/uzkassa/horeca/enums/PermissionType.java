package uz.uzkassa.horeca.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.util.Arrays;
import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum PermissionType {
    ADMIN {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz, en -> "Admin";
                case cyrillic -> "Админ";
                default -> "Админ";
            };
        }
    },
    CABINET {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz, en -> "Cabinet";
                case cyrillic -> "Кабинет";
                default -> "Кабинет";
            };
        }
    },
    RECIPIENT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Buyurtma oluvchi";
                case en -> "Order Recipient";
                case cyrillic -> "Буюртма олувчи";
                default -> "Заказ принимающий";
            };
        }
    },
    EXECUTOR {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Buyurtmani bajaruvchi";
                case en -> "Order executor";
                case cyrillic -> "Буюртмани бажарувчи";
                default -> "Заказ исполняющий";
            };
        }
    };

    private final String code;
    private final String name;

    PermissionType() {
        this.code = this.name();
        this.name = getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }

    public static PermissionType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown PermissionType enum value: " + code));
    }

    @JsonCreator
    public static PermissionType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for PermissionType");
    }
    public static PermissionType getByCode(String code) {
        return Arrays.stream(PermissionType.values()).filter(status -> status.getCode().equals(code)).findFirst().orElse(null);
    }
}
