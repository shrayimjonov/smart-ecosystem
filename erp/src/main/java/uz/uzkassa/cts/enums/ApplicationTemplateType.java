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
public enum ApplicationTemplateType implements Serializable {

    DEVICE_REPLACEMENT_ACT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "NKM almashtirish akti";
                case uz_cyrillic -> "НКМ алмаштириш акти";
                default -> "Акт замены ККМ";
            };
        }
    },
    FISCAL_MODULE_REPLACEMENT_ACT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "FM almashtirish akti";
                case uz_cyrillic -> "ФМ алмаштириш акти";
                default -> "Акт замены ФМ";
            };
        }
    },
    DEREGISTER {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ro'yxatdan chiqarish";
                case uz_cyrillic -> "Рўйхатдан чиқариш";
                default -> "Снятие с регистрации";
            };
        }
    },
    DEVICE_REPLACEMENT_TEMPORARY_ACT {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Remont qilish uchun vaqtinchalik almashtirish";
                case uz_cyrillic -> "Ремонт қилиш учун вақтинчалик алмаштириш";
                default -> "Заменить KKM временно для исправления";
            };
        }
    },
    CHANGE_CUSTOMER_BRANCH_ADDRESS {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Savdo shahobchasi manzilini o'zgartitish";
                case uz_cyrillic -> "Савдо шахобчаси манзилини узгартириш";
                default -> "Изменить адрес торговой точки";
            };
        }
    };

    private final String code;
    private final String name;

    ApplicationTemplateType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static ApplicationTemplateType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown ApplicationTemplateTypeEnum enum value: " + code));
    }

    @JsonCreator
    public static ApplicationTemplateType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for ApplicationTemplateTypeEnum");
    }
}
