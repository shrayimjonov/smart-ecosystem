package uz.uzkassa.smartpos.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.uzkassa.smartpos.domain.postgres.warehouse.MarkingHistory;
import uz.uzkassa.smartpos.enums.AdjustmentReason;
import uz.uzkassa.smartpos.enums.DocumentType;
import uz.uzkassa.smartpos.enums.MarkingStatus;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * DTO for {@link MarkingHistory}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarkingHistoryDto implements Serializable {
    private Long id;
    private String createdBy;
    private LocalDateTime createdDate = LocalDateTime.now();
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate = LocalDateTime.now();
    private DocumentType documentType;
    private AdjustmentReason reason;
    private String document;
    private LocalDate documentDate;
    @NotNull
    private Long companyId;
    private Long branchId;
    private MarkingStatus status = MarkingStatus.ARRIVED;
    private Set<MarkingHistoryItemDto> marks = new HashSet<>();
}
