package uz.uzkassa.smartpossupply.dto.equipment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.EquipmentType;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EquipmentShortDetailDTO {

    EquipmentType type;
}
