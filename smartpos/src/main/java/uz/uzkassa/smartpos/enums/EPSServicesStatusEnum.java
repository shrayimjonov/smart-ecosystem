package uz.uzkassa.smartpos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import uz.uzkassa.smartpos.integration.enums.LanguageEnum;
import uz.uzkassa.smartpos.utils.LanguageUtils;

import java.io.Serializable;
import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EPSServicesStatusEnum implements Serializable {
    ACTIVE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Faol";
                case cyrillic -> "Фаол";
                default -> "Активный";
            };
        }
    },
    INACTIVE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Faol emas";
                case cyrillic -> "Фаол эмас";
                default -> "Неактивный";
            };
        }
    };

    private final String name;
    private final String code;

    EPSServicesStatusEnum() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(LanguageUtils.getLanguage());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static EPSServicesStatusEnum forValue(Map<String, String> value) {
        return EPSServicesStatusEnum.valueOf(value.get("code"));
    }
}
