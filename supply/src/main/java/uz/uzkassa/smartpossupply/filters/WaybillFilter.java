package uz.uzkassa.smartpossupply.filters;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.WaybillStatus;
import uz.uzkassa.smartpossupply.enums.WaybillType;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WaybillFilter extends BaseFilter {

    WaybillStatus waybillStatus;

    WaybillType waybillType;

    boolean outgoing=true;

    String consignorTin;

    String consigneeTin;
}
