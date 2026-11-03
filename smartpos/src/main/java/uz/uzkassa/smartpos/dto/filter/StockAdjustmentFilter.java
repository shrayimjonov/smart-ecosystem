package uz.uzkassa.smartpos.dto.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.StockAdjustmentReason;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class StockAdjustmentFilter extends BaseFilter {
    private Long productId;
    private StockAdjustmentReason reason;
    @Getter(AccessLevel.NONE)
    private Set<Long> branchIds;
    private Set<Long> branchGroupIds;

    public Set<Long> getBranchGroupIds() {
        if (branchGroupIds == null) branchGroupIds = new HashSet<>();
        return branchGroupIds;
    }

    public Set<Long> getBranchIdsWithBranchId() {
        if (branchIds == null) branchIds = new HashSet<>();
        if (branchId != null) branchIds.add(branchId);
        return branchIds;
    }
}
