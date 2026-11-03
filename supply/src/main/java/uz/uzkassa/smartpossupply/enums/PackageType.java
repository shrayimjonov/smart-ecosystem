package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Map;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
@Getter
public enum PackageType implements Serializable {
    BOX(0) {
        @Override
        public String getName(LanguageEnum language) {
            return "Коробка";
        }
    },
    PALLET(1) {
        @Override
        public String getName(LanguageEnum language) {
            return "Паллет";
        }
    },
    BLOCK(2) {
        @Override
        public String getName(LanguageEnum language) {
            return "Блок";
        }
    },
    BOTTLE(99) {
        @Override
        public String getName(LanguageEnum language) {
            return "Бутылок";
        }
    },
    UNIT(3) {
        @Override
        public String getName(LanguageEnum language) {
            return "Потребительская. Пачка/бутылка/штучная единица";
        }
    },
    LEVEL1(4) {
        @Override
        public String getName(LanguageEnum language) {
            return "Блок";
        }
    },
    LEVEL2(5) {
        @Override
        public String getName(LanguageEnum language) {
            return "Коробка";
        }
    },
    LEVEL3(6) {
        @Override
        public String getName(LanguageEnum language) {
            return "Паллет";
        }
    },
    GROUP(7) {
        @Override
        public String getName(LanguageEnum language) {
            return "Группавоя упаковка";
        }
    },
    SET(8) {
        @Override
        public String getName(LanguageEnum language) {
            return "Набор";
        }
    },
    BOX_LV_1(9) {
        @Override
        public String getName(LanguageEnum language) {
            return "Транспортная упаковка 1-го уровня";
        }
    },
    BOX_LV_2(10) {
        @Override
        public String getName(LanguageEnum language) {
            return "Транспортная упаковка 2-го уровня";
        }
    };

    private final String code;
    private final String name;
    private final Integer codeNumber;

    PackageType(Integer codeNumber) {
        this.code = name();
        this.codeNumber = codeNumber;
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static PackageType forValue(Map<String, String> value) {
        return PackageType.valueOf(value.get("code"));
    }

    public static PackageType getByCodeNumber(int codeNumber) {
        for (PackageType item : PackageType.values()) {
            if (codeNumber == item.getCodeNumber()) {
                return item;
            }
        }
        return BOX;
    }

    public static boolean isPallet(PackageType packageType) {
        return (PackageType.LEVEL3.equals(packageType) || PackageType.PALLET.equals(packageType) || PackageType.BOX_LV_2.equals(packageType));
    }

    public static boolean isBox(PackageType packageType) {
        return (PackageType.LEVEL2.equals(packageType) || PackageType.BOX.equals(packageType) || PackageType.BOX_LV_1.equals(packageType));
    }

    public static boolean isBlock(PackageType packageType) {
        return (PackageType.LEVEL1.equals(packageType) || PackageType.BLOCK.equals(packageType) || PackageType.GROUP.equals(packageType));
    }

    public static boolean isUnit(PackageType packageType) {
        return (PackageType.UNIT.equals(packageType) || PackageType.BOTTLE.equals(packageType));
    }

    public static boolean isAggregation(PackageType packageType) {
        return isPallet(packageType) || isBox(packageType);
    }

    public static boolean isLevel3OrBoxLv2(PackageType packageType) {
        return (PackageType.LEVEL3.equals(packageType)) || (PackageType.BOX_LV_2.equals(packageType));
    }

    public static boolean isChosenBoxes(PackageType packageType) {
        return (PackageType.BOX.equals(packageType)
                || PackageType.BOX_LV_1.equals(packageType)
                || PackageType.PALLET.equals(packageType)
                || PackageType.BOX_LV_2.equals(packageType));
    }
}
