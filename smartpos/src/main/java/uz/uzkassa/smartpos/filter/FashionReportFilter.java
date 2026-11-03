package uz.uzkassa.smartpos.filter;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.filter.PrimaryFilter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class FashionReportFilter extends PrimaryFilter {
    private Long companyId;
    private Set<Long> branchIds;
    private Set<Long> branchGroupIds;
    private Long productId;
    private Long colorGroupId;
    private List<Long> categoryIds;
    private Long userId;
    private LocalDateTime from;
    private LocalDateTime to;
}
