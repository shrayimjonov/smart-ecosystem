package uz.uzkassa.smartpossupply.dto.planroute;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.PlanRouteStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 20.01.2023 13:12
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRouteDetailDTO {

    Long id;

    String planRouteNumber;

    CommonDTO agent;

    List<CommonDTO> expeditors = new ArrayList<>();

    LocalDate fromDate;

    LocalDate toDate;

    List<PlanRouteItemDetailDTO> items = new ArrayList<>();

    PlanRouteStatus status;

    CommonDTO region;

    int totalCount;

    int totalVisitedCount;

    int totalCustomerBranch;

    CommonDTO template;

    @JsonIgnore
    public void addItem(PlanRouteItemDetailDTO planRouteItemDetailDTO) {
        items.add(planRouteItemDetailDTO);
    }


    @JsonIgnore
    public void addExpeditor(CommonDTO expeditor) {
        expeditors.add(expeditor);
    }

}
