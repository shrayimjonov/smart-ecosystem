package uz.uzkassa.smartpos.dto.integration.onec;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OneCIncomeDetail(
        @NotNull Long productId,
        @NotNull Long unitId,
        @NotNull BigDecimal qty,
        BigDecimal costPrice,
        BigDecimal salePrice
) {
}
