package uz.uzkassa.silen.enumeration;

import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import uz.uzkassa.silen.dto.SelectItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

//@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum ProductType implements Serializable {
    //    Alcohol("Спирт этиловый пищевой", 1000, ProductGroup.Alcohol, 13),
//    AlcoholTechnical("Спирт этиловый технический", 1001, ProductGroup.Alcohol, 13),
//    Ethereum("Эфир (ЭАФ)", 1002, ProductGroup.Alcohol, 13),
//    CognacAlcohol("Коньячный спирт", 2000, ProductGroup.Alcohol, 13),
//    WineMaterial("Винматериалы", 7000, ProductGroup.Alcohol, 13),
    Other("Другие алкоголы", 10000, ProductGroup.Alcohol, 13, 17),
    Tincture("Настойка", 10001, ProductGroup.Alcohol, 13, 17),
    //    TinctureBlock("Настойка, блоки", 10002, ProductGroup.Alcohol, 17, CisType.GROUP),
    NaturalWine("Натуральные вина", 8000, ProductGroup.Alcohol, 13, 17),
    //    NaturalWineBlock("Натуральные вина, блоки", 8100, ProductGroup.Alcohol, 17, CisType.GROUP),
    Cognac("Коньяк", 3000, ProductGroup.Alcohol, 13, 17),
    //    CognacBLock("Коньяк, блоки", 3100, ProductGroup.Alcohol, 17, CisType.GROUP),
    Vodka("Водка", 4000, ProductGroup.Alcohol, 13, 17),
    //    VodkaBlock("Водка, блоки", 4001, ProductGroup.Alcohol, 17, CisType.GROUP),
    Wine("Вина", 5000, ProductGroup.Alcohol, 13, 17),
    //    WineBlock("Вина, блоки", 5000, ProductGroup.Alcohol, 17, CisType.GROUP),
    Champagne("Шампанское вино", 5001, ProductGroup.Alcohol, 13, 17),
    //    ChampagneBlock("Шампанское вино, блоки", 5101, ProductGroup.Alcohol, 17, CisType.GROUP),
    Liquor("Ликёры", 6000, ProductGroup.Alcohol, 13, 17),
    //    LiquorBlock("Ликёры, блоки", 6100, ProductGroup.Alcohol, 17, CisType.GROUP),
    LiquorVodka("Ликёр-водка", 6001, ProductGroup.Alcohol, 13, 17),
    //    LiquorVodkaBlock("Ликёр-водка, блоки", 6201, ProductGroup.Alcohol, 17, CisType.GROUP),
    Beer("Пиво", 9000, ProductGroup.Beer, 18, 19),
    //    BeerBlock("Пиво, блоки", 9001, ProductGroup.Beer, 19, CisType.GROUP),
    Cigarette("Сигареты, пачки", 11000, ProductGroup.Tobacco, 4, 3),
    //    CigaretteBlock("Сигареты, блоки", 11001, ProductGroup.Tobacco, 3, CisType.GROUP),
    WaterAndSoftDrinks("Вода и прохладительные напитки", 12000, ProductGroup.Water, 16, 16),
    //    WaterAndSoftDrinksBlock("Вода и прохладительные напитки, блоки", 12000, ProductGroup.Water, 16, CisType.GROUP),
    Medicines("Лекарственные средства", 13000, ProductGroup.Pharma, 5, null),
    Electronics("Бытовая техника", 14000, ProductGroup.Appliances, 24, null);

    private final String nameRu;
    private final Integer code;
    private final Integer templateId;
    private final Integer blockTemplateId;
    private final ProductGroup productGroup;

    ProductType(String nameRu, Integer code, ProductGroup productGroup, Integer templateId, Integer blockTemplateId) {
        this.nameRu = nameRu;
        this.code = code;
        this.blockTemplateId = blockTemplateId;
        this.templateId = templateId;
        this.productGroup = productGroup;
    }

    public static ProductType fromCode(Integer code) {
        for (ProductType productType : ProductType.values()) {
            if (productType.getCode().equals(code)) {
                return productType;
            }
        }
        return null;
    }

    public static ProductType fromName(String name) {
        for (ProductType productType : ProductType.values()) {
            if (productType.getNameRu().equals(name)) {
                return productType;
            }
        }
        return null;
    }

    public static Set<ProductType> byOrganizationTypes(EnumSet<OrganizationType> organizationTypes) {
        if (CollectionUtils.isEmpty(organizationTypes)) {
            return EnumSet.noneOf(ProductType.class);
        }
        EnumSet<ProductType> types = EnumSet.noneOf(ProductType.class);
        for (OrganizationType organizationType : organizationTypes) {
            if (EnumSet.of(OrganizationType.Alcohol, OrganizationType.Delivery, OrganizationType.Other).contains(organizationType)) {
//                types.add(Alcohol);
                types.add(Tincture);
//                types.add(TinctureBlock);
//                types.add(AlcoholTechnical);
//                types.add(Ethereum);
            } else if (EnumSet.of(OrganizationType.Vine, OrganizationType.Wholesale, OrganizationType.AlcoholDistribution, OrganizationType.AlcoholRetail).contains(organizationType)) {
                types.add(Cognac);
//                types.add(CognacBLock);
                types.add(Vodka);
//                types.add(VodkaBlock);
//                types.add(WineBlock);
                types.add(Wine);
                types.add(Liquor);
//                types.add(LiquorBlock);
//                types.add(WineMaterial);
                types.add(NaturalWine);
//                types.add(NaturalWineBlock);
            } else if (EnumSet.of(OrganizationType.Beer, OrganizationType.DistributorOfBeer).contains(organizationType)) {
                types.add(Beer);
//                types.add(BeerBlock);
            } else if (EnumSet.of(OrganizationType.Tobacco, OrganizationType.TobaccoDistribution, OrganizationType.TobaccoRetail).contains(organizationType)) {
                types.add(Cigarette);
//                types.add(CigaretteBlock);
            } else if (EnumSet.of(OrganizationType.MedicinesManufacturer, OrganizationType.DistributorOfMedicines).contains(organizationType)) {
                types.add(Medicines);
            } else if (EnumSet.of(OrganizationType.WaterAndBeverageManufacturer, OrganizationType.DistributorOfWaterAndBeverage).contains(organizationType)) {
                types.add(WaterAndSoftDrinks);
//                types.add(WaterAndSoftDrinksBlock);
            } else if (EnumSet.of(OrganizationType.Electronics, OrganizationType.DistributorOfElectronics).contains(organizationType)) {
                types.add(Electronics);
            }
        }
        return types;
    }

    public static List<SelectItem> getAll() {
        List<SelectItem> items = new ArrayList<>(ProductType.values().length);
        for (ProductType item : ProductType.values()) {
            items.add(item.toSelectItem());
        }
        return items;
    }

    public SelectItem toSelectItem() {
        return new SelectItem(this.name(), this.getNameRu());
    }
}
