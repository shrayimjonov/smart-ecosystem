package uz.uzkassa.smartpos.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.smartpos.dto.SelectItemDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public enum ProductGroup implements Serializable {
    Alcohol("alcohol", 11, 13, 2, "Алкогольная продукция"),
    Beer("beer", 15, 18, 3, "Пиво"),
    Tobacco("tobacco", 3, 4, 1, "Табачная продукция"),
    Appliances("appliances", 18, 24, 6, "Бытовая техника"),
    Pharma("pharma", 7, 5, 4, "Лекарственные средства"),
    Water("water", 13, 16, 5, "Вода и прохладительные напитки"),
    Petrol("petrol", null, null, null, "Petrol products"),
    ;

    private final String extension;
    private final Integer productGroup;
    private final Integer templateId;
    private final Integer markingCode;
    private final String nameRu;

    public static ProductGroup fromName(String name) {
        for (ProductGroup productType : ProductGroup.values()) {
            if (productType.getNameRu().equals(name)) {
                return productType;
            }
        }
        return null;
    }

    public static ProductGroup fromMarkingCode(Integer markingCode) {
        for (ProductGroup productType : ProductGroup.values()) {
            if (productType.getMarkingCode().equals(markingCode)) {
                return productType;
            }
        }
        return null;
    }

    public static List<SelectItemDTO> getAll() {
        List<SelectItemDTO> items = new ArrayList<>(ProductGroup.values().length);
        for (ProductGroup item : ProductGroup.values()) {
            items.add(item.toSelectItem());
        }
        return items;
    }

    public SelectItemDTO toSelectItem() {
        return new SelectItemDTO(this.name(), this.getNameRu());
    }
}
