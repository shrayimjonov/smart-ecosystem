package uz.uzkassa.smartpos.dto.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class UserFilter extends BaseFilter {
    private Set<String> roles;
    private boolean dismissed;
    private boolean activated = true;
    @Getter(AccessLevel.NONE)
    private Set<Long> branchIds;
    private Set<Long> branchGroupIds;

    public Set<Long> getBranchIdsWithBranchId() {
        if (branchIds == null) branchIds = new HashSet<>();
        if (branchId != null) branchIds.add(branchId);
        return branchIds;
    }
}
