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
public enum DocumentStatus implements Serializable {
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
    APPROVED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tasdiqlandi";
                case cyrillic -> "Тасдиқланди";
                default -> "Подтверждено";
            };
        }
    },

    REJECTED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Rad etildi";
                case cyrillic -> "Рад этилди";
                default -> "Отклоненный";
            };
        }
    };

    private final String name;
    private final String code;

    DocumentStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static DocumentStatus forValue(Map<String, String> value) {
        return DocumentStatus.valueOf(value.get("code"));
    }
}
