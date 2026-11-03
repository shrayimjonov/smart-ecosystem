package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 23.07.2024
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum WaybillLocalType implements Serializable {
    STANDART(0) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Standard";
                case cyrillic -> "Стандарт";
                default -> "Стандартный";
            };
        }
    },
    ADDITIONAL(1) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Qo`shimcha";
                case cyrillic -> "Қўшимча";
                default -> "Дополнительный";
            };
        }
    },
    EDITED(4) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tuzatuvchi";
                case cyrillic -> "Тузатувчи";
                default -> "Исправленный";
            };
        }
    };

    private final Integer id;
    private final String name;
    private final String code;

    WaybillLocalType(Integer id) {
        this.id = id;
        this.code = this.name();
        this.name = this.getName();
    }

    public static WaybillLocalType fromCode(Integer code) {
        if (code != null) {
            for (WaybillLocalType item : WaybillLocalType.values()) {
                if (code.equals(item.getId())) {
                    return item;
                }
            }
        }
        return null;
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static WaybillLocalType forValue(Map<String, String> value) {
        return WaybillLocalType.valueOf(value.get("code"));
    }

    public static WaybillLocalType buildById(Integer id) {
        if (id == null) {
            return WaybillLocalType.STANDART;
        }
        return Arrays.stream(values()).filter(invoiceSubType -> invoiceSubType.id.equals(id)).findFirst().orElse(WaybillLocalType.STANDART);
    }
}
