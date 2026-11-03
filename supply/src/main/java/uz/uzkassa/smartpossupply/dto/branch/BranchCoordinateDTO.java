package uz.uzkassa.smartpossupply.dto.branch;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

/**
 * Created time: 05.05.2025 12:05
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BranchCoordinateDTO extends CommonDTO {

    String latitude;

    String longitude;
}
