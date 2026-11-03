package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum CashFlowStatus implements Serializable {
    DRAFT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Qoralama";
                case cyrillic -> "Қоралама";
                default -> "Черновик";
            };
        }
    },
    COMPLETED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Завершено";
                case cyrillic -> "Завершено";
                default -> "Завершено";
            };
        }
    };

    CashFlowStatus() {
        this.name = this.getName();
        this.code = this.name();
    }

    private final String name;

    private final String code;

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    public static CashFlowStatus forValue(Map<String, String> value) {
        return CashFlowStatus.valueOf(value.get("code"));
    }

}
