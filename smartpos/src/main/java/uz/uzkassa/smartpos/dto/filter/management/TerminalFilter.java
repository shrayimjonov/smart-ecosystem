package uz.uzkassa.smartpos.dto.filter.management;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.filter.PrimaryFilter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TerminalFilter extends PrimaryFilter {
    private Long branchId;
    private Long companyId;
    private LocalDateTime from;
    private LocalDateTime billFrom;
    private LocalDateTime to;
    private LocalDateTime billTo;
}
