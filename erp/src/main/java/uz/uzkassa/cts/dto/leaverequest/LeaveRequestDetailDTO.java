package uz.uzkassa.cts.dto.leaverequest;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.approvalprocess.ApproverDetailDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.ApproverCommonStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LeaveRequestDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 6674836274512345L;

    Long id;

    String leaveRequestNumber;

    String description;

    LocalDateTime fromDate;

    LocalDateTime toDate;

    Integer duration;

    CommonDTO creator;

    CommonDTO reason;

    ApproverCommonStatus status;

    ApproverDetailDTO approver;

}
