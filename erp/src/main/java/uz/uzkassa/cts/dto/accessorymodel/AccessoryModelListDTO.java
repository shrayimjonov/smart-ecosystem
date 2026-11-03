package uz.uzkassa.cts.dto.accessorymodel;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.CommonStatus;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccessoryModelListDTO extends AccessoryModelDTO {

    CommonStatus status;
}
