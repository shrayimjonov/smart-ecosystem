package uz.uzkassa.smartpossupply.dto.category;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.EnvironmentType;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryEnvironmentDTO extends CommonDTO {

    EnvironmentType environmentType;
}
