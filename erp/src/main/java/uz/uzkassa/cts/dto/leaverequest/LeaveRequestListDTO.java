package uz.uzkassa.cts.dto.leaverequest;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.ApproverCommonStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LeaveRequestListDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1300488273422L;

    Long id;

    String leaveRequestNumber;

    CommonDTO creator;

    String description;

    LocalDateTime fromDate;

    LocalDateTime toDate;

    Integer duration;

    CommonDTO reason;

    ApproverCommonStatus status;

    CommonDTO company;

    CommonDTO department;

    CommonDTO position;

}
