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
 * Date: 10.07.2023
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum RelationType implements Serializable {
    MANUAL {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Qo'lda";
                case cyrillic -> "Қўлда";
                default -> "Вручную";
            };
        }
    },
    FACTURA {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Hisob faktura bo'yicha";
                case cyrillic -> "Ҳисоб фактура бўйича";
                default -> "По счет-фактуре";
            };
        }
    },
    AGGREGATION {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Aggregatsiya bo'yicha";
                case cyrillic -> "Аггрегатсия бўйича";
                default -> "По агрегации";
            };
        }
    },
    AIC {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Aic bo'yicha";
                case cyrillic -> "Аиc бўйича";
                default -> "По АИК";
            };
        }
    },
    ACT {
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Act bo'yicha";
                case cyrillic -> "Аcт бўйича";
                default -> "По акту";
            };
        }
    };
    private final String name;
    private final String code;

    RelationType() {
        this.code = this.name();
        this.name = this.getName();
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static RelationType forValue(Map<String, String> value) {
        return RelationType.valueOf(value.get("code"));
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }
}
