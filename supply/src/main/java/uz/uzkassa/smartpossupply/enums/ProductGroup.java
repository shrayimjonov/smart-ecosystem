package uz.uzkassa.smartpossupply.enums;

import lombok.Getter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public enum ProductGroup implements Serializable {
    Alcohol("alcohol", 11, 2, "Алкогольная продукция"),
    Beer("beer", 15, 3, "Пиво"),
    Tobacco("tobacco", 3, 1, "Табачная продукция"),
    Water("water", 13, 5, "Вода и прохладительные напитки"),
    Pharma("pharma", 7, 4, "Лекарственные средства"),
    Appliances("appliances", 18, 6, "Бытовая техника"),
    Fertilizers("fertilizers", 53, 7, "Минеральные удобрения и средства защиты растений");

    private final String extension;
    private final Integer productGroup;
    private final Integer markingCode;
    private final String name;

    ProductGroup(String extension, Integer productGroup, Integer markingCode, String name) {
        this.extension = extension;
        this.productGroup = productGroup;
        this.markingCode = markingCode;
        this.name = name;
    }

    public static List<Map<String, String>> getProductGroupList() {
        return Arrays.stream(ProductGroup.values())
                .map(pg -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("code", pg.name());
                    map.put("name", pg.getName());
                    return map;
                })
                .toList();
    }
}
