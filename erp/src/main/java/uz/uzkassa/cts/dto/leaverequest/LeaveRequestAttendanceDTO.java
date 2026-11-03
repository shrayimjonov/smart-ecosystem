package uz.uzkassa.cts.dto.leaverequest;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.LeaveRequestReasonAbbreviation;

/**
 * Created date time: 17.02.2025 18:08
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LeaveRequestAttendanceDTO extends CommonDTO {

    LeaveRequestReasonAbbreviation abbreviation;
}
