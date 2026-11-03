
package uz.uzkassa.smartpossupply.dto.waybill;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.acceptancetransferact.AcceptanceTransferActLinkDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.EnumDTO;
import uz.uzkassa.smartpossupply.dto.factura.supply.FacturaLinkDTO;
import uz.uzkassa.smartpossupply.enums.WaybillDeliveryType;
import uz.uzkassa.smartpossupply.enums.WaybillType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupplyWaybillDTO implements Serializable {
    Long id;

    WaybillDTO waybillDTO;

    EnumDTO status;

    WaybillType waybillType;

    WaybillDeliveryType deliveryTypeEnum;

    String note;

    List<WaybillExecutorDTO> executors = new ArrayList<>();

    FacturaLinkDTO factura;

    AcceptanceTransferActLinkDTO shipment;

    CommonDTO stockTransfer;

    public void addExecutor(WaybillExecutorDTO waybillExecutorDTO) {
        this.executors.add(waybillExecutorDTO);
    }
}
