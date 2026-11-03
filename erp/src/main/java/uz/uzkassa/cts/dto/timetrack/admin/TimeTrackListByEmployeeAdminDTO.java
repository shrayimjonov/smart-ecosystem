package uz.uzkassa.cts.dto.timetrack.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TimeTrackListByEmployeeAdminDTO implements Serializable {

    Long id;

    LocalDateTime checkIn;

    LocalDateTime checkOut;

    String imageUrl;

    String thumbnailUrl;

    String videoUrl;
}
