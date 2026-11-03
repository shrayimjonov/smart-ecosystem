package uz.uzkassa.smartpossupply.dto.planroute;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.time.LocalDateTime;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 02.02.2023 12:27
 */
@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRouteItemBaseDTO {

    Long id;

    CommonDTO planRoute;

    LocalDateTime visitDate;
}
