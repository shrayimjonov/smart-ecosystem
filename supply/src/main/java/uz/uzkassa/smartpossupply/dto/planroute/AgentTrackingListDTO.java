package uz.uzkassa.smartpossupply.dto.planroute;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.LocationStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 11.12.2024
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AgentTrackingListDTO implements Serializable {

    Long id;

    Double longitude;

    Double latitude;

    LocalDateTime trackingDate;

    String address;

    LocationStatus status;

    EntityTypeEnum entityType;

    Long entityId;
}