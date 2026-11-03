package uz.uzkassa.cts.dto.timeslot.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.TimeslotType;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TimeslotListAdminDTO extends CommonDTO implements Serializable {

    CommonStatus status;

    TimeslotType type;

    CommonDTO creator;

    LocalDateTime createdDate;
}
