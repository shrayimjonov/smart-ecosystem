package uz.uzkassa.smartpossupply.dto.user.supervisor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupervisorEmployeeAssignDTO {
    Set<Long> employeeIds;
}
