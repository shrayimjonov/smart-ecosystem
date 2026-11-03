package uz.uzkassa.cts.dto.employeetimeslot;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeTimeslotDTO implements Serializable {

    Long employeeId;

    Long timeslotId;
}
