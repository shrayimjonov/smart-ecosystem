package uz.uzkassa.smartpossupply.dto.equipment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.EquipmentType;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EquipmentListMobileDTO extends CommonDTO {

    String brand;

    EquipmentType type;

    CommonDTO customerBranch;
}
