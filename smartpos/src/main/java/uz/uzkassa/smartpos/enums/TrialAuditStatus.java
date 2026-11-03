package uz.uzkassa.smartpos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import uz.uzkassa.smartpos.integration.enums.LanguageEnum;
import uz.uzkassa.smartpos.utils.LanguageUtils;
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TrialAuditStatus {
    ON {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Ulanish";
                case cyrillic -> "Уланиш";
                default -> "Подключение";
            };
        }
    },

    OFF {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Oʻchirish";
                case cyrillic -> "Ўчириш";
                default -> "Отключение";
            };
        }
    };

    final String name;
    @Getter
    final String code;


    TrialAuditStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static TrialAuditStatus forValue(@JsonProperty("code") String code) {
        return TrialAuditStatus.valueOf(code);
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(LanguageUtils.getLanguage());
    }

}
