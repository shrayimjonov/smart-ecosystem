package uz.uzkassa.cts.dto.timetrack;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@FieldDefaults(level = PRIVATE)
public class CameraTimeTrackDTO implements Serializable {

    Long userId;

    LocalDateTime dateTime;

    String type;

    String imageUrl;

    String thumbnailUrl;

    String videoUrl;

    String eventExternalId;

    String cameraIp;
}
