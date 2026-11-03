package uz.uzkassa.cts.dto.equipmentrequest;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;

/**
 * Created date time: 25.12.2024 15:30
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EquipmentRequestItemDetailDTO implements Serializable {

    String description;

    CommonDTO product;

    Integer quantity;
}
