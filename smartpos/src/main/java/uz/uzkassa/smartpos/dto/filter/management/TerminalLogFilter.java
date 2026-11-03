package uz.uzkassa.smartpos.dto.filter.management;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.filter.PrimaryFilter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TerminalLogFilter extends PrimaryFilter {
    private LocalDateTime logDate;
}
