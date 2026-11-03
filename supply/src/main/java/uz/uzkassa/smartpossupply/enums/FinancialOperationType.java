package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by: Shuxratjon Rayimjonov
 * Date: 08.01.2024
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum FinancialOperationType implements Serializable {
    DEBIT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Приход денег";
                case cyrillic -> "Приход денег";
                default -> "Приход денег";
            };
        }
    },
    CREDIT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Расход денег";
                case cyrillic -> "Расход денег";
                default -> "Расход денег";
            };
        }
    };

    private final String name;
    private final String code;

    FinancialOperationType() {
        this.name = this.getName();
        this.code = this.name();
    }

    @JsonCreator
    public static FinancialOperationType forValue(Map<String, String> value) {
        return FinancialOperationType.valueOf(value.get("code"));
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

}
