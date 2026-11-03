package uz.uzkassa.cts.dto.ledger.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class IncomeExpenseRequestDto {
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
}
