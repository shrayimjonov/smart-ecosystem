package uz.uzkassa.smartpossupply.dto.order.terminal;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class TerminalOrderStatisticsDTO {
    Long countCompletedOrders;
    Long countAllOrders;
}
