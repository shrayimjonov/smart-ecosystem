package uz.uzkassa.smartpos.dto.warehouse.fashion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.enums.TransferStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TransferListDto {
    private Long id;
    private TransferStatus status;
    private LocalDateTime transferDate;
    private CommonDTO fromBranch;
    private CommonDTO toBranch;
}
