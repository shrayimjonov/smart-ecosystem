package uz.uzkassa.cts.dto.timeslot;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@FieldDefaults(level = PRIVATE)
public class TimeslotItemDTO implements Serializable {

    Long id;

    int day;

    @JsonFormat(pattern = "HH:mm")
    String fromTime;

    @JsonFormat(pattern = "HH:mm")
    String toTime;

    @JsonFormat(pattern = "HH:mm")
    String lunchFromTime;

    @JsonFormat(pattern = "HH:mm")
    String lunchToTime;
}
