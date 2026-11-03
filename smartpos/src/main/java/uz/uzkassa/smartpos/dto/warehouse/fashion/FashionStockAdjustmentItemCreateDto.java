package uz.uzkassa.smartpos.dto.warehouse.fashion;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.warehouse.FashionStockAdjustmentItem}
 */
@Getter
@Setter
public class FashionStockAdjustmentItemCreateDto implements Serializable {
    @NotNull
    private Long productId;
    private Long productItemId;
    @NotNull
    private Long unitId;
    @Positive
    private BigDecimal qty;
}