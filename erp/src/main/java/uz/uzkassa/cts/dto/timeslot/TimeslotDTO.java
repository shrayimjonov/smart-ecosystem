package uz.uzkassa.cts.dto.timeslot;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.TimeslotType;

import java.io.Serializable;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@FieldDefaults(level = PRIVATE)
public class TimeslotDTO extends CommonDTO implements Serializable {

    Long entityId;

    EntityTypeEnum entityType;

    TimeslotType type = TimeslotType.STANDARD;

    Integer workingHour;

    Integer lunchHour;

    @JsonFormat(pattern = "HH:mm")
    String fromTime;

    @JsonFormat(pattern = "HH:mm")
    String toTime;

    List<TimeslotItemDTO> items;
}
