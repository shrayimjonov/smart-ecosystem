package uz.uzkassa.silen.dto.mq;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.domain.Aggregation;
import uz.uzkassa.silen.domain.mongo.Mark;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.enumeration.WarehouseOperation;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehousePayload implements Serializable {
    String organizationId;
    String productId;
    LocalDateTime operationDate;
    BigDecimal alcoholCount;
    WarehouseOperation operation;
    String note;
    PackageType unit;
    String code;
    String serialId;
    String login;
    AggregationStatus aggregationStatus;
    Long storeId;
    /**
     * if you want to create history but won't update stock
     * set updateStock = false
     */
    boolean updateStock = true;

    public PackageType getPackageType() {
        return unit != null ? unit : PackageType.BOTTLE;
    }

    public static WarehousePayload returnBox(Aggregation aggregation) {
        // add to warehouse like returned
        WarehousePayload payload = new WarehousePayload();
        payload.setOperation(WarehouseOperation.Returned);
        payload.setNote("Возвращен");
        payload.setUnit(PackageType.BOX);
        payload.setAlcoholCount(BigDecimal.valueOf(aggregation.getQuantity()));
        payload.setOperationDate(LocalDateTime.now());
        payload.setOrganizationId(aggregation.getOrganizationId());
        payload.setProductId(aggregation.getProductId());
        payload.setCode(aggregation.getCode());
        return payload;
    }

    public static WarehousePayload returnPallet(Aggregation aggregation) {
        //send to warehouse
        WarehousePayload payload = new WarehousePayload();
        payload.setOperation(WarehouseOperation.Returned);
        payload.setNote("Возврат продукцию");
        payload.setUnit(PackageType.PALLET);
        payload.setOperationDate(LocalDateTime.now());
        payload.setOrganizationId(aggregation.getOrganizationId());
        payload.setProductId(aggregation.getProductId());
        payload.setCode(aggregation.getCode());
        payload.setAlcoholCount(BigDecimal.ZERO);
        return payload;
    }

    public static WarehousePayload dropoutMark(Mark mark) {
        WarehousePayload payload = new WarehousePayload();
        payload.setOperation(WarehouseOperation.Dropout);
        payload.setNote("Потребителская упаковка списано");
        payload.setUnit(PackageType.BOTTLE);
        payload.setAlcoholCount(BigDecimal.ONE);
        payload.setOperationDate(LocalDateTime.now());
        payload.setOrganizationId(mark.getOrganizationId());
        payload.setProductId(mark.getProductId());
        payload.setCode(mark.getCode());
        return payload;
    }
}
