package uz.uzkassa.horeca.enums;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum ProductGroup implements Serializable {
    Alcohol("alcohol", 11, 2, "Алкогольная продукция", ProductType.Alcohol, ProductType.AlcoholBlock),
    Beer("beer", 15, 3, "Пиво", ProductType.Beer, ProductType.BeerBlock),
    Tobacco("tobacco", 3, 1, "Табачная продукция", ProductType.Cigarette, ProductType.CigaretteBlock),
    Water("water", 13, 5, "Вода и прохладительные напитки", ProductType.Water),
    Pharma("pharma", 7, 4, "Лекарственные средства", ProductType.Pharma),
    Appliances("appliances", 18, 6, "Бытовая техника", ProductType.Appliances);

    private final String extension;
    private final Integer productGroup;
    private final Integer markingCode;
    private final String name;
    private final ProductType[] productTypes;

    ProductGroup(String extension, Integer productGroup, Integer markingCode, String name, ProductType... productTypes) {
        this.extension = extension;
        this.productGroup = productGroup;
        this.markingCode = markingCode;
        this.name = name;
        this.productTypes = productTypes;
    }
}
