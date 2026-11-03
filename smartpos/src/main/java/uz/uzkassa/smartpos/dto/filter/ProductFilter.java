package uz.uzkassa.smartpos.dto.filter;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class ProductFilter extends BaseFilter {
    private Long categoryId;
    private Long productId;
    private List<Long> categoryIds;
    private boolean onlyFavorites;
    private boolean creditEnabled;
    private boolean advanceEnabled;
    private Set<Long> branchIds;
    private Set<Long> branchGroupIds;
    private Map<Long, Long> branchCityMap;
    private Map<String, String> attributes;
    private Map<String, String> itemAttributes;
    private boolean available;

    public Set<Long> getBranchIds() {
        if (branchIds == null) {
            branchIds = new HashSet<>();
        }
        if (branchId != null) {
            branchIds.add(branchId);
        }
        return branchIds;
    }
}
