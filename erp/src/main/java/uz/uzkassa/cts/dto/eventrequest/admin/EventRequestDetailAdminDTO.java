package uz.uzkassa.cts.dto.eventrequest.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.eventrequest.EventRequestItemDetailDTO;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created date time: 26.12.2024 11:36
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestDetailAdminDTO implements Serializable {

    Long id;

    String eventRequestNumber;

    CommonDTO company;

    LocalDateTime fromDate;

    LocalDateTime toDate;

    Integer memberCount;

    String room;

    CommonDTO approver;

    CommonDTO creator;

    LocalDateTime createdDate;

    List<EventRequestItemDetailDTO> items;
}
