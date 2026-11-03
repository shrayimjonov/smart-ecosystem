package uz.uzkassa.smartpossupply.dto.kpi.detail;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.kpi.PlanRouteBonusDTO;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KPIPlanRouteDetailDTO extends KPIDetailDTO {
    Long id;

    List<PlanRouteBonusDTO> bonuses;

    List<KPIPlanRouteItemDetailDTO> items;
}
