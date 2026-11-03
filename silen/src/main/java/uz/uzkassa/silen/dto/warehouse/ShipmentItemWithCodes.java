package uz.uzkassa.silen.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.uzkassa.silen.domain.redis.RedisShipmentItem;
import uz.uzkassa.silen.enumeration.PackageType;

import java.io.Serializable;
import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentItemWithCodes implements Serializable {
    private RedisShipmentItem shipmentItem;
    private Set<String> codes;
    private PackageType unit;
    private int quantity;
    private int capacity;
}
