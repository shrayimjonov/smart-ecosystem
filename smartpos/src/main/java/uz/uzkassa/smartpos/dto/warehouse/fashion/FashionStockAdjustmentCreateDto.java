package uz.uzkassa.smartpos.dto.warehouse.fashion;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.StockAdjustmentReason;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.warehouse.FashionStockAdjustmentItem}
 */
@Getter
@Setter
public class FashionStockAdjustmentCreateDto implements Serializable {
    @NotNull
    private Long branchId;
    @NotNull
    private StockAdjustmentReason reason;
    @Valid
    private List<FashionStockAdjustmentItemCreateDto> items;
}