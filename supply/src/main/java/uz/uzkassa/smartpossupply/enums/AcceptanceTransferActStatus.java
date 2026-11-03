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
public enum AcceptanceTransferActStatus implements Serializable {
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
    SENT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yuborilgan";
                case cyrillic -> "Юборилган";
                default -> "Отправлено";
            };
        }
    },
    CANCEL {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Qaytarib olingan";
                case cyrillic -> "Қайтариб олинган";
                default -> "Отозвано";
            };
        }
    },
    ACCEPT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Qabul qilingan";
                case cyrillic -> "Қабул қилинган";
                default -> "Принятый";
            };
        }
    },
    REJECT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Rad etilgan";
                case cyrillic -> "Рад этилган";
                default -> "Отклоненный";
            };
        }
    };

    private final String name;
    private final String code;

    AcceptanceTransferActStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static AcceptanceTransferActStatus forValue(Map<String, String> value) {
        return AcceptanceTransferActStatus.valueOf(value.get("code"));
    }
}
