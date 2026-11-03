package uz.uzkassa.cts.dto.accessorymodel;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class AccessoryModelDTO extends CommonDTO {

    String description;
}
