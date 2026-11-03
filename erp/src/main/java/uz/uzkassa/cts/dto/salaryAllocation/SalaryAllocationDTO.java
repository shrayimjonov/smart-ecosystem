package uz.uzkassa.cts.dto.salaryAllocation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.hrms.SalaryAllocationItem;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SalaryAllocationDTO {

    Long id;

    Long departmentId;

    List<SalaryAllocationItemDTO> items;
}

