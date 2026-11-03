package uz.uzkassa.smartpossupply.dto.expeditor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.user.FullNameDTO;
import uz.uzkassa.smartpossupply.enums.Role;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExpeditorDetailDTO extends FullNameDTO {

    Long id;

    Role role;

    CommonDTO branch;

    String deviceSerialNumber;

    String fiscalModuleSerialNumber;
}
