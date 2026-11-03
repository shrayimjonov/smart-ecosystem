package uz.uzkassa.cts.dto.salaryAllocation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SalaryAllocationItemDTO {

    Long id;

    Long positionId;

    Long gradeId;

    BigDecimal minSalaryAmount;

    BigDecimal maxSalaryAmount;

    Integer minEmployeeCount;

    Integer maxEmployeeCount;

}
