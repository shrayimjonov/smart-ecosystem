package uz.uzkassa.cts.dto.eventrequest;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * Created date time: 26.12.2024 11:35
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestItemDTO implements Serializable {

    String description;

    Long productId;

    Integer quantity;
}
