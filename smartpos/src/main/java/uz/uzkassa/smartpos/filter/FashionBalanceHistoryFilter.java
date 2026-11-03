package uz.uzkassa.smartpos.filter;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.filter.PrimaryFilter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class FashionBalanceHistoryFilter extends PrimaryFilter {
    private Long companyId;
    private Set<Long> branchIds;
    private Set<Long> branchGroupIds;
    private Long productId;
    private List<Long> categoryIds;
    private Long userId;
    private LocalDate balanceDate;
    private LocalDate from;
    private LocalDate to;
}
