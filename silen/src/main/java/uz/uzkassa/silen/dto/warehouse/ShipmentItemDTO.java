package uz.uzkassa.silen.dto.warehouse;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.ShipmentStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.OrderProduct} entity.
 */
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShipmentItemDTO implements Serializable {
    Long id;

    String shipmentId;

    String shipmentNumber;

    String note;

    ShipmentStatus shipmentStatus;

    String shipmentStatusName;

    String productId;

    String productName;

    String packageType;

    BigDecimal qty;

    BigDecimal orderQty;

    LinkedHashSet<ShipmentCode> types;

    Set<String> codes;

    String barcode;

    String parentBarcode;

    String serialId;

    String serialNumber;

}
