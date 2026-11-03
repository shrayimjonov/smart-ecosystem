package uz.uzkassa.cts.dto.salaryAllocation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SalaryAllocationDetailDTO {

    Long id;

    CommonDTO department;

    BigDecimal minSalaryTotalAmount;

    BigDecimal maxSalaryTotalAmount;

    Integer minEmployeeTotalCount;

    Integer maxEmployeeTotalCount;

    List<SalaryAllocationItemDetailDTO> items = new ArrayList<>();

    @JsonIgnore
    public void addItem(SalaryAllocationItemDetailDTO item) {
        items.add(item);
    }
}

