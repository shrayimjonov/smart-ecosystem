package uz.uzkassa.smartpossupply.dto.planroute;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.LocationStatus;

import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AgentTrackingOfflineLocationDTO implements Serializable {

    @NotNull
    Double longitude;

    @NotNull
    Double latitude;

    @NotNull
    LocationStatus status;

    @NotNull
    LocalDateTime createdDate;
}
