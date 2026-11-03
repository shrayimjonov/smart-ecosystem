package uz.uzkassa.cts.dto.event.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created date time: 26.12.2024 17:47
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventDetailAdminDTO implements Serializable {

    Long id;

    CommonDTO company;

    CommonDTO eventRequest;

    String subject;

    String description;

    LocalDateTime fromDate;

    LocalDateTime toDate;

    String guest;

    CommonDTO creator;

    LocalDateTime createdDate;

    List<CommonDTO> participants;
}
