package uz.uzkassa.silen.enumeration;

import lombok.Getter;
import uz.uzkassa.silen.dto.SelectItem;

import java.io.Serializable;
import java.util.*;

@Getter
public enum ProductGroup implements Serializable {
    Alcohol("alcohol", 11, 13, 2, "Алкогольная продукция",
            ProductType.Cognac, ProductType.Vodka, ProductType.Wine,
            ProductType.Champagne, ProductType.Liquor, ProductType.LiquorVodka,
            ProductType.NaturalWine, ProductType.Tincture,
            ProductType.Other
    ),
    Beer("beer", 15, 18, 3, "Пиво", ProductType.Beer),
    Tobacco("tobacco", 3, 4, 1, "Табачная продукция", ProductType.Cigarette),
    Appliances("appliances", 18, 24, 6, "Бытовая техника", ProductType.Electronics),
    Pharma("pharma", 7, 5, 4, "Лекарственные средства", ProductType.Medicines),
    Water("water", 13, 16, 5, "Вода и прохладительные напитки", ProductType.WaterAndSoftDrinks);

    ProductGroup(String extension, Integer productGroup, Integer templateId, Integer markingCode, String nameRu, ProductType... productTypes) {
        this.extension = extension;
        this.productGroup = productGroup;
        this.templateId = templateId;
        this.markingCode = markingCode;
        this.nameRu = nameRu;
        this.productTypes = productTypes;
    }

    private final static Map<ProductGroup, EnumSet<MarkStatus>> validMarkStatusForAggregation = new HashMap<>(ProductGroup.values().length);
    private final static Map<ProductGroup, EnumSet<MarkStatus>> validMarkStatusForShip = new HashMap<>(ProductGroup.values().length);

    static {
        validMarkStatusForAggregation.put(Appliances, EnumSet.of(MarkStatus.INTRODUCED, MarkStatus.APPLIED, MarkStatus.EMITTED));
        validMarkStatusForAggregation.put(Beer, EnumSet.of(MarkStatus.INTRODUCED, MarkStatus.APPLIED, MarkStatus.EMITTED));
        validMarkStatusForAggregation.put(Alcohol, EnumSet.of(MarkStatus.APPLIED, MarkStatus.EMITTED));
        validMarkStatusForAggregation.put(Pharma, EnumSet.of(MarkStatus.APPLIED, MarkStatus.EMITTED));
        validMarkStatusForAggregation.put(Water, EnumSet.of(MarkStatus.APPLIED, MarkStatus.EMITTED));
        validMarkStatusForAggregation.put(Tobacco, EnumSet.of(MarkStatus.INTRODUCED, MarkStatus.APPLIED, MarkStatus.EMITTED));

        validMarkStatusForShip.put(Appliances, EnumSet.of(MarkStatus.INTRODUCED, MarkStatus.APPLIED));
        validMarkStatusForShip.put(Beer, EnumSet.of(MarkStatus.INTRODUCED, MarkStatus.APPLIED));
        validMarkStatusForShip.put(Alcohol, EnumSet.of(MarkStatus.INTRODUCED, MarkStatus.APPLIED));
        validMarkStatusForShip.put(Pharma, EnumSet.of(MarkStatus.INTRODUCED, MarkStatus.APPLIED));
        validMarkStatusForShip.put(Water, EnumSet.of(MarkStatus.INTRODUCED, MarkStatus.APPLIED));
        validMarkStatusForShip.put(Tobacco, EnumSet.of(MarkStatus.INTRODUCED, MarkStatus.APPLIED));

    }

    private final String extension;
    private final Integer productGroup;
    private final Integer templateId;
    private final Integer markingCode;
    private final String nameRu;
    private final ProductType[] productTypes;

    public static ProductGroup fromName(String name) {
        for (ProductGroup productType : ProductGroup.values()) {
            if (productType.getNameRu().equals(name)) {
                return productType;
            }
        }
        return null;
    }

    public static List<SelectItem> getAll() {
        List<SelectItem> items = new ArrayList<>(ProductGroup.values().length);
        for (ProductGroup item : ProductGroup.values()) {
            items.add(item.toSelectItem());
        }
        return items;
    }

    /**
     * Check MarkStatus by returned from trueApi
     *
     * @param markStatus @see {@link MarkStatus}
     * @return boolean
     */
    public boolean checkIsCanShipCodeByStatus(MarkStatus markStatus) {
        return validMarkStatusForShip.get(this).contains(markStatus);
    }

    /**
     * Check MarkStatus by returned from trueApi
     *
     * @param markStatus @see {@link MarkStatus}
     * @return boolean
     */
    public boolean checkIsCanAggregateCodeByStatus(MarkStatus markStatus) {
        return validMarkStatusForAggregation.get(this).contains(markStatus);
    }

    public SelectItem toSelectItem() {
        return new SelectItem(this.name(), this.getNameRu());
    }
}
