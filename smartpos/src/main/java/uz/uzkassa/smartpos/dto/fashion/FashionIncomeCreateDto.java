package uz.uzkassa.smartpos.dto.fashion;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.fashion.FashionIncomeOfProduct}
 */
public record FashionIncomeCreateDto(
        @NotNull Long branchId,
        @NotNull LocalDateTime incomeDate,
        String documentNo,
        String documentDate,
        String description,
        @NotEmpty @Valid List<FashionIncomeDetailCreateDto> details
) implements Serializable {
}