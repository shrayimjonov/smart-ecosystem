package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.List;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SalesPlanType {
    BY_AMOUNT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Summaviy";
                case cyrillic -> "Суммавий";
                default -> "Суммавой";
            };
        }
    },

    QUANTITATIVE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Miqdoriy";
                case cyrillic -> "Миқдорий";
                default -> "Количественный";
            };
        }
    };

        private String name;
        private String code;

        SalesPlanType() {
            this.code = this.name();
            this.name = this.getName();
        }

        public abstract String getName(LanguageEnum language);

        public String getName() {
            return getName(SecurityUtils.getCurrentRequestLanguageEnum());
        }

        public static List<SalesPlanType> getSalesPlanType() {
            return List.of(values());
        }
}
