package uz.uzkassa.smartpossupply.dto.user.supervisor;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommonStatisticsDTO implements Serializable {
    Long countVisitedPlanRoutes = 0L;
    Long countAllPlanRoutes = 0L;
    Long countOrders = 0L;
    Long countDeliveredOrders = 0L;

    public CommonStatisticsDTO(Long countVisitedPlanRoutes, Long countAllPlanRoutes) {
        this.countVisitedPlanRoutes = countVisitedPlanRoutes;
        this.countAllPlanRoutes = countAllPlanRoutes;
    }
}
