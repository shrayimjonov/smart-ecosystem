package uz.uzkassa.smartpossupply.dto.planroute;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date 05.12.2023
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRouteCheckLocationDTO {

    @NotNull
    Long customerBranchId;

    @NotNull
    Double agentLongitude;

    @NotNull
    Double agentLatitude;
}
