package uz.uzkassa.smartpos.dto.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CabinetTerminalFilter extends PrimaryFilter {
    private Long branchId;
    private Long companyId;
}
