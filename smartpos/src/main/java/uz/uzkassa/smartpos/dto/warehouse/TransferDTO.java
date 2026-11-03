package uz.uzkassa.smartpos.dto.warehouse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpos.dto.BaseAuditDTO;
import uz.uzkassa.smartpos.enums.TransferStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class TransferDTO extends BaseAuditDTO {
    private String description;
    private TransferStatus status;
    private LocalDateTime transferDate;
    private Long fromBranchId;
    private String fromBranchName;
    private Long toBranchId;
    private String toBranchName;
    private Long approvedById;
    private List<TransferDetailDTO> transferDetails;
}
