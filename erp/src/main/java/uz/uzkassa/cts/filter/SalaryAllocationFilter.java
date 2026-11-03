package uz.uzkassa.cts.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SalaryAllocationFilter extends BaseFilter {

    BigDecimal minSalaryTotalAmount;

    BigDecimal maxSalaryTotalAmount;

    Integer minEmployeeTotalCount;

    Integer maxEmployeeTotalCount;

}
