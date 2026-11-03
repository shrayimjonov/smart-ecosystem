package uz.uzkassa.smartpos.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import uz.uzkassa.smartpos.integration.enums.LanguageEnum;
import uz.uzkassa.smartpos.utils.LanguageUtils;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ApayRegisterStatus {
    REGISTERED {
        @Override
        public String getName(LanguageEnum language) {
            if (language == LanguageEnum.ru) {
                return "Зарегистрирован";
            }
            return "Ro'yxatdan o'tgan";
        }
    };

    private final String name;
    private final String code;

    ApayRegisterStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(LanguageUtils.getLanguage());
    }
}
