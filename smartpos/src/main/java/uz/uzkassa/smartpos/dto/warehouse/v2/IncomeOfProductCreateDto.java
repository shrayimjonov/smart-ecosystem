package uz.uzkassa.smartpos.dto.warehouse.v2;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.warehouse.IncomeOfProduct}
 */
@Getter
@Setter
public class IncomeOfProductCreateDto<T> implements Serializable {
    private String documentNo;
    @NotNull
    private LocalDate documentDate;
    private LocalDateTime incomeDate;
    private String description;
    @NotNull
    private Long toBranchId;
    @NotEmpty
    @Valid
    private List<T> incomeOfProductDetails = new ArrayList<>();
    private Long invoiceId;
}