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
public enum SmsStatus implements Serializable {
    PENDING {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.uz) {
                return "Yuborish jarayonida";
            }
            return "В ожидании";
        }
    },

    SENT {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.uz) {
                return "Yuborildi";
            }
            return "Отправил";
        }
    },
    FAILED {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.uz) {
                return "Yoborishda xatolik";
            }
            return "Отправить не удалось";
        }
    };

    private final String name;
    private final String code;

    SmsStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static SmsStatus forValue(Map<String, String> value) {
        return SmsStatus.valueOf(value.get("code"));
    }
}
