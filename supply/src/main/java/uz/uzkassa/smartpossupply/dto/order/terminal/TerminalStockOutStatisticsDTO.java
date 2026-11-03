package uz.uzkassa.smartpossupply.dto.order.terminal;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class TerminalStockOutStatisticsDTO {
    Long countCompletedStocks;
    Long countAllStocks;
    BigDecimal totalPrice;
    BigDecimal totalPriceInCash;
    BigDecimal totalPriceInTransfer;
    Long countStockOutProducts;
}
