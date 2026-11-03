package uz.uzkassa.smartpossupply.dto.planroutetemplate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.DayOfWeekEnum;

import java.util.List;

/**
 * Date 20.01.2023 10:55
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRouteTemplateItemDTO {

    Long id;

    DayOfWeekEnum day;

    List<Long> customerBranches;
}
