package uz.uzkassa.silen.dto.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.ShipmentStatus;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShipmentFilter extends Filter {

    Long customerId;

    ShipmentStatus status;

    String id;

    String productId;

    String storeId;

    String createdBy;
}
