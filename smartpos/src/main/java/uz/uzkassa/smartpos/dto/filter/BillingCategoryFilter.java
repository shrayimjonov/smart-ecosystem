package uz.uzkassa.smartpos.dto.filter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.MultiValueMap;
import uz.uzkassa.smartpos.enums.SortTypeEnum;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class BillingCategoryFilter extends BillingPageFilter implements Serializable {

    private String orderBy = "code";

    SortTypeEnum sortOrder = SortTypeEnum.desc;

    private Boolean active;

    private Boolean parentIsNull;

    public MultiValueMap<String, String> getMultiValueMap() {
        var map = super.getMultiValueMap();
        if (orderBy != null) {
            map.put("orderBy", List.of(orderBy));
        }
        if (sortOrder != null) {
            map.put("sortOrder", List.of(sortOrder.name()));
        }
        if (active != null) {
            map.put("active", List.of(String.valueOf(active)));
        }
        if (parentIsNull != null) {
            map.put("parentIsNull", List.of(String.valueOf(parentIsNull)));
        }
        return map;
    }
}

