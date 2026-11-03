package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 19.01.2023 19:43
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum PlanRouteStatus implements Serializable {

    OPEN {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yakunlanmagan";
                case cyrillic -> "Якунланмаган";
                default -> "Не выполнен";
            };
        }
    },
    PARTIALLY_CLOSED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Qisman yakunlangan";
                case cyrillic -> "Қисман якунланган";
                default -> "Частично выполнен";
            };
        }
    },
    CLOSED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yakunlangan";
                case cyrillic -> "Якунланган";
                default -> "Выполнен";
            };
        }
    };


    private final String name;
    private final String code;

    PlanRouteStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static PlanRouteStatus forValue(Map<String, String> value) {
        return PlanRouteStatus.valueOf(value.get("code"));
    }
}
