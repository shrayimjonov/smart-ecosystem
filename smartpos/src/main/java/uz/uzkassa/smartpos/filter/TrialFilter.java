package uz.uzkassa.smartpos.filter;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.filter.PrimaryFilter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TrialFilter extends PrimaryFilter {
    private Long id;
    private Long companyId;
    private Long userId;
    private LocalDateTime from;
    private LocalDateTime to;
    private LocalDateTime fromCreated;
    private LocalDateTime toCreated;
}
