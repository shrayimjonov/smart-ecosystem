package uz.uzkassa.smartpossupply.dto.planroutetemplate;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

/**
 * Date 20.01.2023 10:55
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRouteTemplateDTO {

    Long id;

    @NotNull
    String name;

    @NotNull
    Long agentId;

    Long regionId;

    @NotNull
    Long branchId;

    @NotEmpty
    Set<PlanRouteTemplateItemDTO> items;

    List<Long> expeditorIds;
}
