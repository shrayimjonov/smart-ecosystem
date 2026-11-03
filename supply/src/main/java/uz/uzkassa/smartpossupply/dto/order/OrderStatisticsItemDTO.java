package uz.uzkassa.smartpossupply.dto.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 18.01.2023 16:03
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderStatisticsItemDTO {
    Long orderTotalCount;

    BigDecimal orderTotalAmount;

    String type;

    @SuppressWarnings("unused")
    public OrderStatisticsItemDTO(Long orderTotalCount, BigDecimal orderTotalAmount, String type) {
        this.orderTotalCount = orderTotalCount;
        this.orderTotalAmount = orderTotalAmount;
        this.type = type;
    }
}
