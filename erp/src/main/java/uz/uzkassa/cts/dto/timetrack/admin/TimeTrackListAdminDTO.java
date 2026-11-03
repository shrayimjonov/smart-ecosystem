package uz.uzkassa.cts.dto.timetrack.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TimeTrackListAdminDTO implements Serializable {

    Long id;

    CommonDTO employee;

    CommonDTO department;

    CommonDTO position;

    LocalDateTime checkIn;

    LocalDateTime checkOut;

    String imageUrl;

    String thumbnailUrl;

    String videoUrl;
}
