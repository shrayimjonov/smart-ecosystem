package uz.uzkassa.smartpossupply.dto.order.mobile;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.customerbranch.CustomerBranchAddressDTO;
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
public class MobileOrderListForExpeditorDTO {

    Long id;

    String orderNumber;

    BigDecimal totalQty;

    BigDecimal totalPrice;

    OrderStatus status;

    CommonDTO customer;

    CommonDTO planRoute;

    CustomerBranchAddressDTO customerBranch;

    LocalDateTime orderDate;

    BigDecimal totalDiscountPrice;
}
