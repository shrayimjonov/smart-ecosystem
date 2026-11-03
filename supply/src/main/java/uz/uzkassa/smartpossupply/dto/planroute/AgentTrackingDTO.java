package uz.uzkassa.smartpossupply.dto.planroute;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;

import java.io.Serializable;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 31.01.2023 15:45
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@AllArgsConstructor
public class AgentTrackingDTO implements Serializable {

    EntityTypeEnum entityType;

    Long entityId;

    Double longitude;

    Double latitude;
}
