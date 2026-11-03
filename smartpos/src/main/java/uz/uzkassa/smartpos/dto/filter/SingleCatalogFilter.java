package uz.uzkassa.smartpos.dto.filter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class SingleCatalogFilter extends BaseFilter {
    String singleCatalogId;
    String productType;
    String categoryId;
    String fieldId;
    boolean containsDefault = false;
    String referenceIds;
    BigDecimal fromPrice;
    BigDecimal toPrice;
    String includeStatuses;
    String parentCategoryId;
    boolean onlyExist = false;

    public MultiValueMap<String, String> getMultiValueMap() {
        var map = super.getMultiValueMap();
        if (singleCatalogId != null) {
            map.put("singleCatalogId", List.of(singleCatalogId));
        }
        if (productType != null) {
            map.put("productType", List.of(productType));
        }
        if (categoryId != null) {
            map.put("categoryId", List.of(categoryId));
        }
        if (fieldId != null) {
            map.put("fieldId", List.of(fieldId));
        }
        map.put("containsDefault", List.of(String.valueOf(containsDefault)));
        if (referenceIds != null) {
            map.put("referenceIds", List.of(referenceIds));
        }
        if (fromPrice != null) {
            map.put("fromPrice", List.of(String.valueOf(fromPrice)));
        }
        if (toPrice != null) {
            map.put("toPrice", List.of(String.valueOf(toPrice)));
        }
        if (includeStatuses != null) {
            map.put("includeStatuses", List.of(includeStatuses));
        }
        if (parentCategoryId != null) {
            map.put("parentCategoryId", List.of(parentCategoryId));
        }
        map.put("onlyExist", List.of(String.valueOf(onlyExist)));
        return map;
    }
}
