package uz.uzkassa.cts.dto.accessory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.AccessoryType;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccessoryLookupDTO extends CommonDTO {
    AccessoryType type;
}
