package uz.uzkassa.smartpos.filter.onec;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.filter.PrimaryFilter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OneCIncomeFilter extends PrimaryFilter {
    private Long branchId;
    private LocalDateTime from;
    private LocalDateTime to;
}
