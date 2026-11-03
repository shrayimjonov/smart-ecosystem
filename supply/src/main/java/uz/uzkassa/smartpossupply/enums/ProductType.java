package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
@Getter
public enum ProductType implements Serializable {
    Alcohol(13, ProductGroup.Alcohol) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Алкоголь";
                case cyrillic -> "Алкоголь";
                default -> "Алкоголь";
            };
        }
    },
    AlcoholBlock(17, ProductGroup.Alcohol) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Алкоголь блок";
                case cyrillic -> "Алкоголь блок";
                default -> "Алкоголь блок";
            };
        }
    },
    Beer(18, ProductGroup.Beer) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Пиво";
                case cyrillic -> "Пиво";
                default -> "Пиво";
            };
        }
    },
    BeerBlock(19, ProductGroup.Beer) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Пиво, блок";
                case cyrillic -> "Пиво, блок";
                default -> "Пиво, блок";
            };
        }
    },
    Cigarette(4, ProductGroup.Tobacco) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Сигареты";
                case cyrillic -> "Сигареты";
                default -> "Сигареты";
            };
        }
    },
    CigaretteBlock(3, ProductGroup.Tobacco) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Сигареты, блок";
                case cyrillic -> "Сигареты, блок";
                default -> "Сигареты, блок";
            };
        }
    },
    Water(16, ProductGroup.Water) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Вода и прохладительные напитки";
                case cyrillic -> "Вода и прохладительные напитки";
                default -> "Вода и прохладительные напитки";
            };
        }
    },
    Pharma(5, ProductGroup.Pharma) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Лекарственные средства";
                case cyrillic -> "Лекарственные средства";
                default -> "Лекарственные средства";
            };
        }
    },
    Appliances(24, ProductGroup.Appliances) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Бытовая техника";
                case cyrillic -> "Бытовая техника";
                default -> "Бытовая техника";
            };
        }
    },
    Fertilizers(null, ProductGroup.Fertilizers) {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Минеральные удобрения и средства защиты растений";
                case cyrillic -> "Минеральные удобрения и средства защиты растений";
                default -> "Минеральные удобрения и средства защиты растений";
            };
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
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static ProductType get(String code) {
        if (!StringUtils.hasText(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown ProductType enum value: " + code));
    }

    @JsonCreator
    public static ProductType fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for ProductType");
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
        } else if (ProductGroup.Fertilizers.getMarkingCode().equals(markingCode)) {
            return ProductType.Fertilizers;
        }
        return null;
    }

    public static List<ProductType> getBlockProductTypes() {
        return Arrays.asList(ProductType.AlcoholBlock, ProductType.BeerBlock, ProductType.CigaretteBlock);
    }
}
