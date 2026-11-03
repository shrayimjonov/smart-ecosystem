package uz.uzkassa.silen.domain.redis;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import uz.uzkassa.silen.dto.warehouse.ShipmentCode;
import uz.uzkassa.silen.dto.warehouse.ShipmentItemDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;

@ToString
@RedisHash("ShipmentCode")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class RedisShipmentItem implements Serializable {
    @Id
    Long id;
    @Indexed
    String shipmentId;
    BigDecimal qty;
    BigDecimal orderQty;
    @Indexed
    String productId;
    String productName;
    String barcode;
    String parentBarcode;
    String serialId;
    String serialNumber;
    LinkedHashSet<ShipmentCode> types;
    LocalDateTime createdAt = LocalDateTime.now();

    public LinkedHashSet<ShipmentCode> getTypes() {
        if (types == null) {
            types = new LinkedHashSet<>();
        }
        return types;
    }

    public ShipmentItemDTO toDto() {
        ShipmentItemDTO dto = new ShipmentItemDTO();
        dto.setId(getId());
        dto.setShipmentId(getShipmentId());
        dto.setProductId(getProductId());
        dto.setTypes(getTypes());
        dto.setQty(getQty());
        dto.setOrderQty(getOrderQty());
        dto.setProductName(getProductName());
        dto.setBarcode(getBarcode());
        dto.setParentBarcode(getParentBarcode());
        dto.setSerialId(getSerialId());
        dto.setSerialNumber(getSerialNumber());
        return dto;
    }
}
