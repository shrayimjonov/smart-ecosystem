package uz.uzkassa.smartpossupply.dto.warehouseproduct;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;

import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 11.12.2023
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseProductHistoryListDTO {

    Long id;

    Long entityId;

    String stockNumber;

    EntityTypeEnum entityType;

    BigDecimal qty;

    @SuppressWarnings("unused")
    public WarehouseProductHistoryListDTO(Long id, Long entityId, String stockNumber, String entityType, BigDecimal qty) {
        this.id = id;
        this.entityId = entityId;
        this.stockNumber = stockNumber;
        this.entityType = entityType != null ? EntityTypeEnum.valueOf(entityType) : null;
        this.qty = qty;
    }
}
