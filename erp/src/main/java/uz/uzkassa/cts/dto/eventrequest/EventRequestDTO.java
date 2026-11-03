package uz.uzkassa.cts.dto.eventrequest;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created date time: 26.12.2024 11:35
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestDTO implements Serializable {

    String subject;

    LocalDateTime fromDate;

    LocalDateTime toDate;

    Long approverId;

    Long companyId;

    Integer memberCount;

    String room;

    List<EventRequestItemDTO> items;
}
