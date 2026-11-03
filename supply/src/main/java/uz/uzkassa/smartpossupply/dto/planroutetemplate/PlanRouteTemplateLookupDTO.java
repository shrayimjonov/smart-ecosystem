package uz.uzkassa.smartpossupply.dto.planroutetemplate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;

import java.time.LocalDateTime;

/**
 * Date 20.01.2023 10:55
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRouteTemplateLookupDTO extends CommonDTO {

    String templateNumber;

    CommonDTO branch;

    LocalDateTime createdDate;

    CommonStatus status;
}
