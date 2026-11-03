package uz.uzkassa.cts.dto.leaveallowance;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class BaseLeaveAllowanceDTO {

    Long id;

    Integer totalAllowance;

    String totalAllowanceTitle;

    Integer remainingAllowance;

    String remainingAllowanceTitle;

    Integer year;
}
