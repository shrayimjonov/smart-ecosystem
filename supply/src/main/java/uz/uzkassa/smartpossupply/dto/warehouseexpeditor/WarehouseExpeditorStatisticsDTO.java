package uz.uzkassa.smartpossupply.dto.warehouseexpeditor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 29.11.2024
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseExpeditorStatisticsDTO {

    Long totalExpeditorCount;

    Long totalPlanRouteCount;

    Long totalOrderCount;

    Long totalCompletedOrderCount;

    public WarehouseExpeditorStatisticsDTO(Long totalExpeditorCount, Long totalPlanRouteCount, Long totalOrderCount, Long totalCompletedOrderCount) {
        this.totalExpeditorCount = totalExpeditorCount;
        this.totalPlanRouteCount = totalPlanRouteCount;
        this.totalOrderCount = totalOrderCount;
        this.totalCompletedOrderCount = totalCompletedOrderCount;
    }
}