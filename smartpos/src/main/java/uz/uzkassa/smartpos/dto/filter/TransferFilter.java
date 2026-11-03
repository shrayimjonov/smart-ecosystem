package uz.uzkassa.smartpos.dto.filter;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.TransferStatus;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class TransferFilter extends PrimaryFilter {
    private Long companyId;
    private TransferStatus status;
    private Set<Long> branchIds;
    private Set<Long> branchGroupIds;
    private LocalDateTime from;
    private LocalDateTime to;
    private Long toBranchId;
    private Long fromBranchId;
}
