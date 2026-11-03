package uz.uzkassa.horeca.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.io.Serializable;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
@Getter
public enum ProductType implements Serializable {
    Alcohol(13, ProductGroup.Alcohol) {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "Алкоголь";
                case cyrillic:
                    return "Алкоголь";
                default:
                    return "Алкоголь";
            }
        }
    },
    AlcoholBlock(17, ProductGroup.Alcohol) {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "Алкоголь блок";
                case cyrillic:
                    return "Алкоголь блок";
                default:
                    return "Алкоголь блок";
            }
        }
    },
    Beer(18, ProductGroup.Beer) {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "Пиво";
                case cyrillic:
                    return "Пиво";
                default:
                    return "Пиво";
            }
        }
    },
    BeerBlock(19, ProductGroup.Beer) {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "Пиво, блок";
                case cyrillic:
                    return "Пиво, блок";
                default:
                    return "Пиво, блок";
            }
        }
    },
    Cigarette(4, ProductGroup.Tobacco) {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "Сигареты";
                case cyrillic:
                    return "Сигареты";
                default:
                    return "Сигареты";
            }
        }
    },
    CigaretteBlock(3, ProductGroup.Tobacco) {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "Сигареты, блок";
                case cyrillic:
                    return "Сигареты, блок";
                default:
                    return "Сигареты, блок";
            }
        }
    },
    Water(16, ProductGroup.Water) {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "Вода и прохладительные напитки";
                case cyrillic:
                    return "Вода и прохладительные напитки";
                default:
                    return "Вода и прохладительные напитки";
            }
        }
    },
    Pharma(5, ProductGroup.Pharma) {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "Лекарственные средства";
                case cyrillic:
                    return "Лекарственные средства";
                default:
                    return "Лекарственные средства";
            }
        }
    },
    Appliances(24, ProductGroup.Appliances) {
        @Override
        public String getName(LanguageEnum language) {
            switch (language) {
                case uz:
                    return "Бытовая техника";
                case cyrillic:
                    return "Бытовая техника";
                default:
                    return "Бытовая техника";
            }
        }
    };

    private final String name;
    private final String code;
    private final Integer templateId;
    private final ProductGroup productGroup;

    ProductType(Integer templateId, ProductGroup productGroup) {
        this.name = this.getName();
        this.code = name();
        this.templateId = templateId;
        this.productGroup = productGroup;
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(HeaderUtils.getCurrentRequestLanguageEnum());
    }

    public static ProductType buildByMarkingCode(Integer markingCode) {
        if (ProductGroup.Alcohol.getMarkingCode().equals(markingCode)) {
            return ProductType.Alcohol;
        } else if (ProductGroup.Beer.getMarkingCode().equals(markingCode)) {
            return ProductType.Beer;
        } else if (ProductGroup.Tobacco.getMarkingCode().equals(markingCode)) {
            return ProductType.Cigarette;
        } else if (ProductGroup.Water.getMarkingCode().equals(markingCode)) {
            return ProductType.Water;
        } else if (ProductGroup.Pharma.getMarkingCode().equals(markingCode)) {
            return ProductType.Pharma;
        } else if (ProductGroup.Appliances.getMarkingCode().equals(markingCode)) {
            return ProductType.Appliances;
        }
        return null;
    }
}
