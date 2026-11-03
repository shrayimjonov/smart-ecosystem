package uz.uzkassa.smartpos.dto.warehouse;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.base.CommonDTO;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.warehouse.MarkingIncome}
 */
@Getter
@Setter
public class MarkingIncomeDto implements Serializable {
    private Long id;
    private CommonDTO branch;
    private LocalDateTime createdDate;
    private LocalDate operationDate;
    private CommonDTO createdBy;
    private List<MarkingIncomeDetailDto> products;
}