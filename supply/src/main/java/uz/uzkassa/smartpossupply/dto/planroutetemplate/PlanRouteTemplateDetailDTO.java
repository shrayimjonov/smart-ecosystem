package uz.uzkassa.smartpossupply.dto.planroutetemplate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRouteTemplateDetailDTO extends CommonDTO {

    String templateNumber;

    CommonDTO agent;

    List<CommonDTO> expeditors = new ArrayList<>();

    CommonDTO region;

    CommonDTO branch;

    CommonStatus status;

    List<PlanRouteTemplateItemDetailDTO> items = new ArrayList<>();

    @JsonIgnore
    public void addItem(PlanRouteTemplateItemDetailDTO planRouteTemplateItemDetailDTO) {
        items.add(planRouteTemplateItemDetailDTO);
    }

    @JsonIgnore
    public void addExpeditor(CommonDTO expeditor) {
        expeditors.add(expeditor);
    }
}
