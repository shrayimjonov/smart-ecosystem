package uz.uzkassa.smartpossupply.dto.planroute;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date 20.01.2023 10:55
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRouteDTO {

    Long id;

    Long templateId;

    String name;

    Long agentId;

    LocalDate fromDate;

    LocalDate toDate;

    Set<PlanRouteItemDTO> items;

    Long regionId;

    List<Long> expeditorIds;
}
