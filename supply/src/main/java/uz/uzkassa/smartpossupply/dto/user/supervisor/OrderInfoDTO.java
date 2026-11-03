package uz.uzkassa.smartpossupply.dto.user.supervisor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderInfoDTO {
    Long id;
    String orderNumber;
    CommonDTO customer;
    CommonDTO customerBranch;
    CommonDTO expeditor;
    CommonDTO agent;
    CommonDTO planRoute;
    LocalDateTime orderDate;
    OrderStatus status;
    BigDecimal totalPrice;
}
