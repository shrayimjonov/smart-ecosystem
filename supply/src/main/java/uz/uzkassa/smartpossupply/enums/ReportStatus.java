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
 * Date: 15.02.2023 14:53
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum ReportStatus implements Serializable {
    PENDING {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tasdiqlanish jarayonida";
                case cyrillic -> "Тасдиқланиш жараёнида";
                default -> "В ожидании";
            };
        }
    },
    ERROR {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Xatolik";
                case cyrillic -> "Xатолик";
                default -> "Ошибка";
            };
        }
    },
    COMPLETED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Bajarildi";
                case cyrillic -> "Бажарилди";
                default -> "Завершен";
            };
        }
    };

    private final String name;
    private final String code;

    ReportStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static ReportStatus forValue(Map<String, String> value) {
        return ReportStatus.valueOf(value.get("code"));
    }

}
