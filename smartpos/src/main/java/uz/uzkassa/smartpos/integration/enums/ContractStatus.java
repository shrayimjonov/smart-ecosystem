package uz.uzkassa.smartpos.integration.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import uz.uzkassa.smartpos.utils.LanguageUtils;

import java.io.Serializable;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ContractStatus implements Serializable {
    ACCEPTED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tasdiqlangan";
                case cyrillic -> "Тасдиқланган";
                default -> "Подтверждено";
            };
        }
    },
    REJECTED {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Rad etilgan";
                case cyrillic -> "Рад этилган";
                default -> "Отклонено";
            };
        }
    },
    INACTIVE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Faol emas";
                case cyrillic -> "Фаол эмас";
                default -> "Неактивный";
            };
        }
    };

    private final String name;
    private final String code;

    ContractStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(LanguageUtils.getLanguage());
    }
}
