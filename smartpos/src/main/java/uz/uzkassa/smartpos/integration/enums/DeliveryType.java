package uz.uzkassa.smartpos.integration.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import uz.uzkassa.smartpos.utils.LanguageUtils;

import java.io.Serializable;
import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum DeliveryType implements Serializable {
    DELIVERY {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.uz) {
                return "Yetkazib berish";
            }
            return "Доставка";
        }
    },
    EXPRESS_DELIVERY {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.uz) {
                return "Tez yetkazib berish";
            }
            return "Срочная Доставка";
        }
    },
    SELF_PICKUP {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.uz) {
                return "O`zi olib ketish";
            }
            return "Самовывоз";
        }
    };

    private final String name;
    private final String code;

    DeliveryType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(LanguageUtils.getLanguage());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static DeliveryType forValue(Map<String, String> value) {
        return DeliveryType.valueOf(value.get("code"));
    }
}
