package uz.uzkassa.smartpossupply.dto.equipment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.file.FileDTO;
import uz.uzkassa.smartpossupply.enums.EquipmentType;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EquipmentDetailDTO extends CommonDTO {

    String brand;

    String inventoryNumber;

    EquipmentType type;

    CommonDTO customerBranch;

    FileDTO photo;
}
