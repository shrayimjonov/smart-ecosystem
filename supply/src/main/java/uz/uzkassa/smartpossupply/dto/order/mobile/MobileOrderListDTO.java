package uz.uzkassa.smartpossupply.dto.order.mobile;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 19.01.2023 11:24
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MobileOrderListDTO {

    Long id;

    String orderNumber;

    CommonDTO customer;

    LocalDateTime orderDate;

    OrderStatus status;

    BigDecimal totalPrice;

    BigDecimal totalDiscountPrice;

    LocalDateTime deliveryDate;

    LocalDateTime lastModifiedDate;
}
