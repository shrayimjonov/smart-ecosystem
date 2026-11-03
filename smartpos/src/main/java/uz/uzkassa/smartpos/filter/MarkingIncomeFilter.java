package uz.uzkassa.smartpos.filter;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.filter.PrimaryFilter;
import uz.uzkassa.smartpos.enums.MarkingWarehouseStatus;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class MarkingIncomeFilter extends PrimaryFilter {
    private Long companyId;
    private Set<Long> branchIds;
    private Set<Long> branchGroupIds;
    private Long productId;
    private Long userId;
    private LocalDate from;
    private LocalDate to;
    private MarkingWarehouseStatus status;
}
