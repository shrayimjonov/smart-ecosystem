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
public class OrderExpeditorTotalQtyReportDTO {
    Long totalExpeditorCount;
    BigDecimal completedOrderCount;
    BigDecimal orderCount;
    BigDecimal stockOutCount;
    BigDecimal returnedOrderCount;
    BigDecimal totalOrderPrice;
}
