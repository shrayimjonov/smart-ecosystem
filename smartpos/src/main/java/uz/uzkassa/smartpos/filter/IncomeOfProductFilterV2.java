package uz.uzkassa.smartpos.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.filter.PrimaryFilter;
import uz.uzkassa.smartpos.enums.IncomeOfProductStatus;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class IncomeOfProductFilterV2 extends PrimaryFilter {
    private Long companyId;
    @Deprecated
    private Long branchId;
    @Getter(AccessLevel.NONE)
    private Set<Long> branchIds;
    private Set<Long> branchGroupIds;
    private LocalDateTime from;
    private LocalDateTime to;
    private IncomeOfProductStatus incomeStatus;

    public Set<Long> getBranchIdsWithBranchId() {
        if (branchIds == null) branchIds = new HashSet<>();
        if (branchId != null) branchIds.add(branchId);
        return branchIds;
    }
}
