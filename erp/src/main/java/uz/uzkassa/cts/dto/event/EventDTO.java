package uz.uzkassa.cts.dto.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.reminder.ReminderDTO;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created date time: 26.12.2024 17:12
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventDTO implements Serializable {

    String subject;

    String description;

    LocalDateTime fromDate;

    LocalDateTime toDate;

    String guest;

    Long eventRequestId;

    Long companyId;

    List<Long> participantIds;

    List<ReminderDTO> reminders;
}
