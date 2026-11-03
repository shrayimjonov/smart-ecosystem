package uz.uzkassa.smartpos.dto.catalog;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateProductPriceRecord(@NotNull Long id, Long branchId, @NotNull BigDecimal salesPrice) {
}
