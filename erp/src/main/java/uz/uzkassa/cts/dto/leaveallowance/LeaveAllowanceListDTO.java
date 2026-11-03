package uz.uzkassa.cts.dto.leaveallowance;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LeaveAllowanceListDTO extends BaseLeaveAllowanceDTO {

    CommonDTO employee;


}
