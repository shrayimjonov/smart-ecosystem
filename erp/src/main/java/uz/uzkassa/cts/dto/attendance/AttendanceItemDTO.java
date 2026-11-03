package uz.uzkassa.cts.dto.attendance;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.leaverequest.LeaveRequestAttendanceDTO;

import java.io.Serializable;

/**
 * Created date time: 28.01.2025 18:00
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceItemDTO implements Serializable {

    Long id;

    int day;

    Integer plannedHours;

    Integer actualHours;

    CommonDTO holiday;

    LeaveRequestAttendanceDTO leaveRequest;
}
