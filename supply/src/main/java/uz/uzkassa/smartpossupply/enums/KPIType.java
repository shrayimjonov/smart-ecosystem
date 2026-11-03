package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.List;


@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum KPIType {
    SALES_PLAN {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Sotuv plani";
                case cyrillic -> "Сотув плани";
                default -> "План продажа";
            };
        }
    },

    PLAN_ROUTE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Mashrut";
                case cyrillic -> "Машрут";
                default -> "Посещения";
            };
        }
    },

    NEW_CLIENT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yangi mijoz";
                case cyrillic -> "Янги мижоз";
                default -> "Новый клиенты";
            };
        }
    };

    private final String code;
    private final String name;

    KPIType() {
        this.code = this.name();
        this.name = this.getName();
    }


    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static List<KPIType> getKpiTypes() {
        return List.of(KPIType.values());
    }
}
