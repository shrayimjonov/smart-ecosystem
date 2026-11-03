package uz.uzkassa.smartpos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import uz.uzkassa.smartpos.integration.enums.LanguageEnum;
import uz.uzkassa.smartpos.utils.LanguageUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum ProductType implements Serializable {
    Alcohol(13, ProductGroup.Alcohol) {
        @Override
        public String getName(LanguageEnum language) {
            return "Алкоголь";
        }
    },
    AlcoholBlock(17, ProductGroup.Alcohol) {
        @Override
        public String getName(LanguageEnum language) {
            return "Алкоголь блок";
        }
    },
    Beer(18, ProductGroup.Beer) {
        @Override
        public String getName(LanguageEnum language) {
            return "Пиво";
        }
    },
    BeerBlock(19, ProductGroup.Beer) {
        @Override
        public String getName(LanguageEnum language) {
            return "Пиво, блок";
        }
    },
    Cigarette(4, ProductGroup.Tobacco) {
        @Override
        public String getName(LanguageEnum language) {
            return "Сигареты";
        }
    },
    CigaretteBlock(3, ProductGroup.Tobacco) {
        @Override
        public String getName(LanguageEnum language) {
            return "Сигареты, блок";
        }
    },
    Water(16, ProductGroup.Water) {
        @Override
        public String getName(LanguageEnum language) {
            return "Вода и прохладительные напитки";
        }
    },
    Pharma(5, ProductGroup.Pharma) {
        @Override
        public String getName(LanguageEnum language) {
            return "Лекарственные средства";
        }
    },
    Appliances(24, ProductGroup.Appliances) {
        @Override
        public String getName(LanguageEnum language) {
            return "Бытовая техника";
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


    public static boolean isContainGroupMarkOrder(ProductType productType) {
        return Arrays.asList(
            CigaretteBlock,
            BeerBlock,
            AlcoholBlock
        ).contains(productType);
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(LanguageUtils.getLanguage());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static ProductType forValue(Map<String, String> value) {
        return ProductType.valueOf(value.get("code"));
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
