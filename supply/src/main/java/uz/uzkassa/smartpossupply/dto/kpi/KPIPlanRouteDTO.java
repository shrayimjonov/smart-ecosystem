package uz.uzkassa.smartpossupply.dto.kpi;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KPIPlanRouteDTO extends KPIBaseDTO {

    Long id;

    Long companyId;

    List<PlanRouteBonusDTO> bonuses = new ArrayList<>();

    Set<Long> planRouteIds = new HashSet<>();
}
