package uz.uzkassa.smartpossupply.dto.kpi.detail;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KPIPlanRouteItemDetailDTO extends CommonDTO {
    CommonDTO region;
    Integer count;
    CommonDTO employee;
}