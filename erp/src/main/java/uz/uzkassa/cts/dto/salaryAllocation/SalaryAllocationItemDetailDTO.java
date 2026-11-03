package uz.uzkassa.cts.dto.salaryAllocation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SalaryAllocationItemDetailDTO {

    Long id;

    CommonDTO position;

    CommonDTO grade;

    BigDecimal minSalaryAmount;

    BigDecimal maxSalaryAmount;

    Integer minEmployeeCount;

    Integer maxEmployeeCount;
}
