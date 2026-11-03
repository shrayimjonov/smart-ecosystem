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
public enum OfflineOrderStatus implements Serializable {

    SUCCESS {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Muvaffaqiyatli";
                case cyrillic -> "Муваффақиятли";
                default -> "Успешно";
            };
        }
    },
    FAIL {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Muvaffaqiyatsiz";
                case cyrillic -> "Муваффақиятсиз";
                default -> "Неудачно";
            };
        }
    },
    PENDING {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Kutilmoqda";
                case cyrillic -> "Кутилмоқда";
                default -> "В ожидании";
            };
        }
    };

    private final String name;
    private final String code;

    OfflineOrderStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static OfflineOrderStatus forValue(Map<String, String> value) {
        return OfflineOrderStatus.valueOf(value.get("code"));
    }
}
