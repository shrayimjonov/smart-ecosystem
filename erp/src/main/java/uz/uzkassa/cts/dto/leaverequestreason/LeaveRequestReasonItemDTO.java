package uz.uzkassa.cts.dto.leaverequestreason;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.LeaveRequestReasonType;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LeaveRequestReasonItemDTO extends CommonDTO {

    LeaveRequestReasonType type;

}
