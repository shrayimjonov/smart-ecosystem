package uz.uzkassa.smartpossupply.dto.planroute;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 11.12.2024
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRouteAgentTrackingDTO {

    Long agentId;

    LocalDate date;
}
