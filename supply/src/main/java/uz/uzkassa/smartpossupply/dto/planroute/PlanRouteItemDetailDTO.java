package uz.uzkassa.smartpossupply.dto.planroute;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.DayOfWeekEnum;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 20.01.2023
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRouteItemDetailDTO {

    Long id;

    LocalDate visitPlanDate;

    DayOfWeekEnum day;

    List<PlanRouteCustomerBranchDetailDTO> customerBranches = new ArrayList<>();

    @JsonIgnore
    public void addCustomerBranch(PlanRouteCustomerBranchDetailDTO planRouteCustomerBranch) {
        customerBranches.add(planRouteCustomerBranch);
    }
}
