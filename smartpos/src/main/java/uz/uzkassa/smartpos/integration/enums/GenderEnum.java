package uz.uzkassa.smartpos.integration.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import uz.uzkassa.smartpos.utils.LanguageUtils;

import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum GenderEnum {
    MALE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Erkak";
                case cyrillic -> "Эркак";
                default -> "Мужской";
            };
        }
    },

    FEMALE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Ayol";
                case cyrillic -> "Аёл";
                default -> "Женский";
            };
        }
    };

    private final String code;
    private final String name;

    GenderEnum() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(LanguageUtils.getLanguage());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static GenderEnum forValue(Map<String, String> value) {
        return GenderEnum.valueOf(value.get("code"));
    }
}
