package uz.uzkassa.cts.dto.event.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.EventStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created date time: 26.12.2024 17:57
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventListAdminDTO implements Serializable {

    Long id;

    EventStatus status;

    String subject;

    String description;

    LocalDateTime fromDate;

    LocalDateTime toDate;

    String guest;

//    Long eventRequestId;

    CommonDTO creator;

    LocalDateTime createdDate;
}
