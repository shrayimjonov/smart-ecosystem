package uz.uzkassa.smartpos.filter;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.filter.PrimaryFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class FashionRegisterFilter extends PrimaryFilter {
    private Long companyId;
    private Set<Long> branchIds = new HashSet<>();
    private Set<Long> branchGroupIds = new HashSet<>();
    private Long productId;
    private Long colorGroupId;
    private List<Long> categoryIds;

}
