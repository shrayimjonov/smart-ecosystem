package uz.uzkassa.smartpossupply.dto.kpi.detail;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.KPIType;
import uz.uzkassa.smartpossupply.enums.Role;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class KPIDetailDTO {
    Role role;

    KPIType type;

    String name;

    CommonDTO region;
}
