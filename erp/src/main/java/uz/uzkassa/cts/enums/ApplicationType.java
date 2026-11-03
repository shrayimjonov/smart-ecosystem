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
public enum ApplicationType implements Serializable {

    REGISTER(4) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "NKMni ro'yxatga olish";
                case uz_cyrillic -> "НКМни рўйхатга олиш";
                default -> "Регистрация ККМ";
            };
        }
    },
    CHANGE_ADDRESS(5) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Manzilni o'zgartirish";
                case uz_cyrillic -> "Манзилни ўзгаритиш";
                default -> "Изменить адрес";
            };
        }
    },
    DEREGISTER(6) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Ro'yxatdan chiqarish";
                case uz_cyrillic -> "Рўйхатдан чиқариш";
                default -> "Снятие с регистрации";
            };
        }
    },
    DEREGISTER_DEVICE(7) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "NKMni ro'yxatdan chiqarish";
                case uz_cyrillic -> "НКМни рўйхатдан чиқариш";
                default -> "Снятие KKM с регистрации";
            };
        }
    },
    DEREGISTER_FISCAL_MODULE(8) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "FMni ro'yxatdan chiqarish";
                case uz_cyrillic -> "ФМни рўйхатдан чиқариш";
                default -> "Снятие ФМ с регистрации";
            };
        }
    };

    private final String code;
    private final String name;
    private final Integer id;

    ApplicationType(Integer id) {
        this.code = this.name();
        this.name = this.getName();
        this.id = id;
    }

    public static ApplicationType getById(Integer id) {
        if (id == null) {
            return REGISTER;
        }
        for (ApplicationType applicationType : ApplicationType.values()) {
            if (id.equals(applicationType.getId())) {
                return applicationType;
            }
        }
        return REGISTER;
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static ApplicationType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown ApplicationTypeEnum enum value: " + code));
    }

    @JsonCreator
    public static ApplicationType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for ApplicationTypeEnum");
    }
}
