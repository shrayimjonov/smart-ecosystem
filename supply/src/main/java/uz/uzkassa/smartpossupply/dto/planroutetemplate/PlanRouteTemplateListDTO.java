package uz.uzkassa.smartpossupply.dto.planroutetemplate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Date 20.01.2023 10:55
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRouteTemplateListDTO extends CommonDTO {

    String templateNumber;

    CommonDTO agent;

    List<CommonDTO> expeditors = new ArrayList<>();

    CommonDTO region;

    CommonDTO branch;

    LocalDateTime createdDate;

    CommonStatus status;

    int customerBranchCount;

    public void addExpeditor(CommonDTO expeditor) {
        expeditors.add(expeditor);
    }
}
