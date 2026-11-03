package uz.uzkassa.smartpossupply.dto.planroutetemplate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.DayOfWeekEnum;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRouteTemplateItemDetailDTO implements Comparable<PlanRouteTemplateItemDetailDTO> {

    Long id;

    DayOfWeekEnum day;

    List<PlanRouteTemplateCustomerBranchDetailDTO> customerBranches = new ArrayList<>();

    @JsonIgnore
    public void addItem(PlanRouteTemplateCustomerBranchDetailDTO planRouteTemplateCustomerBranchDetailDTO) {
        customerBranches.add(planRouteTemplateCustomerBranchDetailDTO);
    }

    @Override
    public int compareTo(PlanRouteTemplateItemDetailDTO o) {
        return Integer.compare(this.day.ordinal(), o.getDay().ordinal());
    }
}
