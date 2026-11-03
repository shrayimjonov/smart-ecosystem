package uz.uzkassa.smartpos.filter.specification;

import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.smartpos.enums.SortTypeEnum;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSpecificationFilter {
    @Builder.Default
    private boolean dismissed = false;
    @Builder.Default
    private Boolean activated = Boolean.TRUE;
    private Set<String> roles;
    private Long companyId;
    private Set<Long> branchIds;
    private Set<Long> branchGroupIds;
    private LocalDateTime from;
    private LocalDateTime to;
    @Builder.Default
    private boolean owner = false;
    private String search;
    private String orderBy;
    @Builder.Default
    private SortTypeEnum sortOrder = SortTypeEnum.desc;

    public String getSearchForQuery() {
        if (StringUtils.isNotBlank(search)) {
            return "%" + search.toLowerCase() + "%";
        } else {
            return null;
        }
    }

    public void requiredRoles(Set<String> requiredRoles) {
        if (CollectionUtils.isNotEmpty(roles)) {
            roles.removeIf(role -> !requiredRoles.contains(role));
        }
        if (CollectionUtils.isEmpty(roles)) {
            roles = requiredRoles;
        }
    }
}
