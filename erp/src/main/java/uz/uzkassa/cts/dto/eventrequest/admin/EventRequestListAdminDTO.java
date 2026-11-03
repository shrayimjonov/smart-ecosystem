package uz.uzkassa.cts.dto.eventrequest.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.ApproverCommonStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created date time: 26.12.2024 11:36
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestListAdminDTO implements Serializable {

    Long id;

    String eventRequestNumber;

    ApproverCommonStatus status;

    LocalDateTime fromDate;

    LocalDateTime toDate;

    Integer memberCount;

    String room;

    CommonDTO approver;

    CommonDTO creator;

    LocalDateTime createdDate;
}
