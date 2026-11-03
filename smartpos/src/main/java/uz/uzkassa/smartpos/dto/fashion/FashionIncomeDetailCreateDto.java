package uz.uzkassa.smartpos.dto.fashion;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.fashion.FashionIncomeOfProductDetail}
 */
public record FashionIncomeDetailCreateDto(
        @NotNull Long productId,
        @NotNull Long unitId,
        @NotNull BigDecimal qty,
        @NotNull BigDecimal costPrice,
        @NotNull BigDecimal salesPrice,
        Long productItemId
) implements Serializable {
}