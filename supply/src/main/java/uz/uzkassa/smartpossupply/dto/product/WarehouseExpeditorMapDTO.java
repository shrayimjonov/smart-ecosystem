package uz.uzkassa.smartpossupply.dto.product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 05.12.2024
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseExpeditorMapDTO implements Serializable {

    Long expeditorId;

    String expeditorFirstName;

    String expeditorLastName;

    Long totalOrderCount;

    Long totalCompletedOrderCount;

    BigDecimal totalQty;

    BigDecimal totalSoldQty;


    Long carId;

    String carName;

    String carRegNo;

    String carModel;

    @SuppressWarnings("unused")
    public WarehouseExpeditorMapDTO(Long expeditorId, String expeditorFirstName, String expeditorLastName, Long totalOrderCount, Long totalCompletedOrderCount, BigDecimal totalQty, BigDecimal totalSoldQty) {
        this.expeditorId = expeditorId;
        this.expeditorFirstName = expeditorFirstName;
        this.expeditorLastName = expeditorLastName;
        this.totalOrderCount = totalOrderCount;
        this.totalCompletedOrderCount = totalCompletedOrderCount;
        this.totalQty = totalQty;
        this.totalSoldQty = totalSoldQty;
    }

    @SuppressWarnings("unused")
    public WarehouseExpeditorMapDTO(Long carId, String carName, String carRegNo, String carModel, BigDecimal totalQty, BigDecimal totalSoldQty) {
        this.carId = carId;
        this.carName = carName;
        this.carRegNo = carRegNo;
        this.carModel = carModel;
        this.totalQty = totalQty;
        this.totalSoldQty = totalSoldQty;
    }
}
