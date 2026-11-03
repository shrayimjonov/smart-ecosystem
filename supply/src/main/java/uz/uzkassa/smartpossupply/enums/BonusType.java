package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.List;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BonusType {
    SUM {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Summa";
                case cyrillic -> "Summa";
                default -> "Сумма";
            };
        }
    },

    PERCENTAGE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "prosent";
                case cyrillic -> "Процент";
                default -> "Процент";
            };
        }
    };

    private String name;
    private String code;

    BonusType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static List<BonusType> getBonusTypes() {
        return List.of(values());
    }
}
