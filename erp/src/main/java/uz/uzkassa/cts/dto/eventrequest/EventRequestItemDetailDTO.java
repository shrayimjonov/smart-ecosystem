package uz.uzkassa.cts.dto.eventrequest;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;

/**
 * Created date time: 26.12.2024 11:35
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestItemDetailDTO implements Serializable {

    String description;

    CommonDTO product;

    Integer quantity;
}
