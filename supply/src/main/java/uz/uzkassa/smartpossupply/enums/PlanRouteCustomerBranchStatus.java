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
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
@Getter
public enum PlanRouteCustomerBranchStatus implements Serializable {

    VISITED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tashrif buyirilgan";
                case cyrillic -> "Ташриф буйирилган";
                default -> "Посетил";
            };
        }
    },
    VISITING {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tashrif buyirmoqda";
                case cyrillic -> "Ташриф буйирмоқда";
                default -> "Нa посещение";
            };
        }
    },
    NOT_VISITED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tashrif buyirilmagan";
                case cyrillic -> "Ташриф буйирилмаган";
                default -> "Не посетил";
            };
        }
    },
    NOT_VISITED_YET {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Hali tashrif buyirilmagan";
                case cyrillic -> "Ташриф буйирилмаган";
                default -> "Еще не посетил";
            };
        }
    };

    private final String name;
    private final String code;

    PlanRouteCustomerBranchStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static PlanRouteCustomerBranchStatus forValue(Map<String, String> value) {
        return PlanRouteCustomerBranchStatus.valueOf(value.get("code"));
    }
}
