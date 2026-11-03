package uz.uzkassa.smartpossupply.dto.waybill;


import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.acceptancetransferact.AcceptanceTransferActLinkDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.EnumDTO;
import uz.uzkassa.smartpossupply.dto.factura.supply.FacturaLinkDTO;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WaybillListDTO {
    Long id;

    WaybillSelectItem waybillDoc;

    WaybillSelectItem contractDoc;

    WaybillSelectItem consignee;

    BigDecimal totalDeliveryCost;

    EnumDTO status;

    AcceptanceTransferActLinkDTO shipment;

    FacturaLinkDTO factura;

    CommonDTO stockTransfer;

    CommonDTO branch;
}
