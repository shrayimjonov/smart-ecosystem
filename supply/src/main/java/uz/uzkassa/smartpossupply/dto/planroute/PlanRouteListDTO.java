package uz.uzkassa.smartpossupply.dto.planroute;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.PlanRouteStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date 20.01.2023 10:55
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRouteListDTO {

    Long id;

    String planRouteNumber;

    CommonDTO agent;

    List<CommonDTO> expeditors = new ArrayList<>();

    LocalDate fromDate;

    LocalDate toDate;

    PlanRouteStatus status;

    CommonDTO region;

    LocalDateTime createdDate;

    public void addExpeditor(CommonDTO expeditor) {
        expeditors.add(expeditor);
    }
}
