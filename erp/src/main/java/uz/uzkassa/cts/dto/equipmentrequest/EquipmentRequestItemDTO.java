package uz.uzkassa.cts.dto.equipmentrequest;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * Created date time: 25.12.2024 14:49
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EquipmentRequestItemDTO implements Serializable {

    String description;

    Long productId;

    Integer quantity;
}
