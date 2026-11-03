package uz.uzkassa.silen.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.dto.warehouse.ShipmentCode;
import uz.uzkassa.silen.enumeration.StockInStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;


@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoreInItemListDTO {
    Long id;
    String productId;
    String storeInId;
    String shipmentId;
    Long shipmentItemId;
    String productName;
    String barcode;
    String serialNumber;
    LocalDate serialProductionDate;
    BigDecimal shipmentQty;
    BigDecimal qty;
    StockInStatus status = StockInStatus.NEW;
    LinkedHashSet<ShipmentCode> types = new LinkedHashSet<>();
    Set<StoreInMarkDTO> codes;
}
