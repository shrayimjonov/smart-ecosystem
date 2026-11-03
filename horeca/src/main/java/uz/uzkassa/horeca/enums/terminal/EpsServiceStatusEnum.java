package uz.uzkassa.horeca.enums.terminal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.horeca.enums.LanguageEnum;

import java.io.Serializable;
import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum EpsServiceStatusEnum implements Serializable {
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

    EpsServiceStatusEnum() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(LanguageEnum.ru);
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static EpsServiceStatusEnum forValue(Map<String, String> value) {
        return EpsServiceStatusEnum.valueOf(value.get("code"));
    }
}
