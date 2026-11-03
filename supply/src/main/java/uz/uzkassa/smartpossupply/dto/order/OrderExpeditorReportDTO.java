package uz.uzkassa.smartpossupply.dto.order;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderExpeditorReportDTO {
    CommonDTO branch;
    CommonDTO warehouse;
    CommonDTO expeditor;
    CommonDTO planRoute;
    Long orderCount;
    Long stockOutCount;
    Long returnedOrderCount;
    BigDecimal totalOrderPrice;


    public OrderExpeditorReportDTO(
            Long branchId, String branchName,
            Long warehouseId, String warehouseName,
            Long expeditorId, String expeditorName,
            Long planRouteId, String planRouteName,
            Long orderCount, Long stockOutCount, Long returnedOrderCount, BigDecimal totalOrderPrice) {
        this.branch = new CommonDTO(branchId, branchName);
        this.warehouse = new CommonDTO(warehouseId, warehouseName);
        this.expeditor = new CommonDTO(expeditorId, expeditorName);
        this.planRoute = new CommonDTO(planRouteId, planRouteName);
        this.orderCount = orderCount;
        this.stockOutCount = stockOutCount;
        this.returnedOrderCount = returnedOrderCount;
        this.totalOrderPrice = totalOrderPrice;
    }
}
