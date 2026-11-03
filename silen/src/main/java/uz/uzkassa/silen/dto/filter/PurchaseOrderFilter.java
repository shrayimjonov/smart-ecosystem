package uz.uzkassa.silen.dto.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.PurchaseOrderStatus;
import uz.uzkassa.silen.enumeration.PurchaseOrderType;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PurchaseOrderFilter extends Filter {

    Long customerId;

    PurchaseOrderStatus status;

    Long storeId;

    PurchaseOrderType type;

}
