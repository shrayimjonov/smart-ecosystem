package uz.uzkassa.smartpossupply.dto.order.telegram;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramOrderListDTO {

    Long id;

    String orderNumber;

    OrderStatus status;

    LocalDateTime orderDate;

    BigDecimal totalPrice;
}
