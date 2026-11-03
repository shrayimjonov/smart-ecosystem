package uz.uzkassa.smartpos.dto.warehouse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.warehouse.MarkingIncome}
 */
@Getter
@Setter
public class MarkingIncomeCreateDto implements Serializable {
    @NotNull
    private Long branchId;
    private Long fromBranchId;
    private LocalDate operationDate = LocalDate.now();
    @Valid
    @NotEmpty
    private List<MarkingIncomeDetailCreateDto> details = new ArrayList<>();
}