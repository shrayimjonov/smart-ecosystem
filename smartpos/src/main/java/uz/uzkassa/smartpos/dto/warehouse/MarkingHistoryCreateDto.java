package uz.uzkassa.smartpos.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.uzkassa.smartpos.domain.postgres.warehouse.MarkingHistory;
import uz.uzkassa.smartpos.enums.AdjustmentReason;
import uz.uzkassa.smartpos.enums.DocumentType;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link MarkingHistory}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarkingHistoryCreateDto implements Serializable {
    private DocumentType documentType;
    private AdjustmentReason reason;
    private String document;
    private LocalDate documentDate;
    @NotNull
    private Long companyId;
    private Long branchId;
    private List<MarkingHistoryItemDto> marks;
}
