package uz.uzkassa.smartpossupply.filters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;
import org.springframework.util.MultiValueMap;
import uz.uzkassa.smartpossupply.enums.SortTypeEnum;
import uz.uzkassa.smartpossupply.integration.filter.BillingPageFilter;

import java.util.List;

@Getter
@Setter
public class CategoryFilter extends BillingPageFilter {

    private String orderBy = "code";

    SortTypeEnum sortOrder = SortTypeEnum.desc;

    private Boolean active;

    private Boolean parentIsNull;

    @JsonIgnore
    
    public Sort getOrderedSortBy() {
        return sortOrder.equals(SortTypeEnum.asc) ? Sort.by(orderBy).ascending() : Sort.by(orderBy).descending();
    }

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
