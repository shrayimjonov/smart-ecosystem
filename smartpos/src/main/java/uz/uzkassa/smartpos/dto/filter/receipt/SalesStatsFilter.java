package uz.uzkassa.smartpos.dto.filter.receipt;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.filter.BaseFilter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class SalesStatsFilter extends BaseFilter {
    private Long customerId;
    private Set<Long> branchGroupIds;
    private boolean onlySocial;
    private List<Long> categoryIds;
    @Getter(AccessLevel.NONE)
    private Set<Long> branchIds;
    private Long productId;
    private LocalDate balanceDate;
    private BigDecimal fromSalesTotal;
    private BigDecimal toSalesTotal;
    private BigDecimal qtyTo;
    private BigDecimal qtyFrom;

    public Set<Long> getBranchIdsWithBranchId() {
        if (branchIds == null) branchIds = new HashSet<>();
        if (branchId != null) branchIds.add(branchId);
        return branchIds;
    }

    public Set<Long> getBranchGroupIds() {
        if (branchGroupIds == null) branchGroupIds = new HashSet<>();
        return branchGroupIds;
    }
}
