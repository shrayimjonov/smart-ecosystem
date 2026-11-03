package uz.uzkassa.smartpos.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.uzkassa.smartpos.enums.AdjustmentReason;
import uz.uzkassa.smartpos.enums.DocumentType;
import uz.uzkassa.smartpos.enums.MarkingStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarkingHistoryListDto implements Serializable {
    private Long id;
    private String createdBy;
    private LocalDateTime createdDate = LocalDateTime.now();
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate = LocalDateTime.now();
    private DocumentType documentType;
    private AdjustmentReason reason;
    private String document;
    private String documentDate;
    @NotNull
    private Long companyId;
    private Long branchId;
    private MarkingStatus status = MarkingStatus.ARRIVED;
}
