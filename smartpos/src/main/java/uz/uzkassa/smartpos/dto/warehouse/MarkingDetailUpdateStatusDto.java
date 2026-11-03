package uz.uzkassa.smartpos.dto.warehouse;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.MarkingWarehouseStatus;
import uz.uzkassa.smartpos.enums.StockAdjustmentReason;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.warehouse.MarkingIncomeDetail}
 */
@Getter
@Setter
public class MarkingDetailUpdateStatusDto implements Serializable {
    @NotNull
    private LocalDate operationDate;
    @NotNull
    private MarkingWarehouseStatus status = MarkingWarehouseStatus.RETURNED;
    private StockAdjustmentReason adjustmentReason;
    @NotEmpty
    private Set<String> marks;
}