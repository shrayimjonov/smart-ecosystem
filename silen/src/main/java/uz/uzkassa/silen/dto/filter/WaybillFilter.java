package uz.uzkassa.silen.dto.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.DeliveryType;
import uz.uzkassa.silen.enumeration.WaybillStatus;
import uz.uzkassa.silen.enumeration.WaybillType;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WaybillFilter extends Filter {

    WaybillStatus status;

    WaybillType type;

    DeliveryType deliveryType;
}
