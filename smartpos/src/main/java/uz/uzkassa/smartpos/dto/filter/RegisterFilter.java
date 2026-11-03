package uz.uzkassa.smartpos.dto.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class RegisterFilter extends PrimaryFilter {
    private Long companyId;
    @Deprecated
    private Long branchId;
    private Long productId;
    private Long unitId;
    private List<Long> categoryIds;
    private String mxik;
    @Getter(AccessLevel.NONE)
    private Set<Long> branchIds;
    private Set<Long> branchGroupIds;

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
