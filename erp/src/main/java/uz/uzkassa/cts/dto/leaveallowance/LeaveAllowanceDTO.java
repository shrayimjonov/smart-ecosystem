package uz.uzkassa.cts.dto.leaveallowance;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LeaveAllowanceDTO {

    Long id;

    Long employeeId;

    Integer year;
}
