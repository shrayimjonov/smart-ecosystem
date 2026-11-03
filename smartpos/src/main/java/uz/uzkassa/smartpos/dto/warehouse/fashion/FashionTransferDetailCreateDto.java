package uz.uzkassa.smartpos.dto.warehouse.fashion;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.warehouse.TransferDetail}
 */
public record FashionTransferDetailCreateDto(
        @Positive BigDecimal askedQty,
        @NotNull Long productId,
        @NotNull Long unitId,
        Long productItemId,
        BigDecimal salesPrice) implements Serializable {
}