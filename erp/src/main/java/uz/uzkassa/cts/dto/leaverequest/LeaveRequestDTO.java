package uz.uzkassa.cts.dto.leaverequest;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.approvalprocess.ApproverDTO;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LeaveRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 33445827274L;

    Long id;

    String description;

    Long reasonId;

    LocalDateTime fromDate;

    LocalDateTime toDate;

    List<ApproverDTO> approvers;

}
