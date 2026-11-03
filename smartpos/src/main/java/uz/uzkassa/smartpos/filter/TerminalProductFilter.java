package uz.uzkassa.smartpos.filter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.filter.PrimaryFilter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TerminalProductFilter extends PrimaryFilter {
    private Long branchId;
    @Schema(hidden = true)
    private Long cityId;
    private LocalDateTime from;
}
