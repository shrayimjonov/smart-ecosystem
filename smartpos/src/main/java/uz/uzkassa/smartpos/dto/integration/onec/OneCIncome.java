package uz.uzkassa.smartpos.dto.integration.onec;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record OneCIncome(
        @NotNull Long toBranchId,
        String documentNo,
        LocalDate documentDate,
        @NotEmpty List<OneCIncomeDetail> incomeOfProductDetails
) {
}
