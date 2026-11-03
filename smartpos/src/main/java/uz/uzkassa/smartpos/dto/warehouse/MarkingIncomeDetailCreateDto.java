package uz.uzkassa.smartpos.dto.warehouse;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.warehouse.MarkingIncomeDetail}
 */
@Getter
@Setter
public class MarkingIncomeDetailCreateDto implements Serializable {
    @NotNull
    private Long productId;
    @NotEmpty
    private Set<String> marks;
}