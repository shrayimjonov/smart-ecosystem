package uz.uzkassa.horeca.dto.order.mobile;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonCodeDTO;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.order.cabinet.OrderGrandTotalDTO;
import uz.uzkassa.horeca.dto.reservation.CustomerDetailDTO;
import uz.uzkassa.horeca.dto.terminal.BillListDTO;
import uz.uzkassa.horeca.enums.OrderReason;
import uz.uzkassa.horeca.enums.OrderStatus;
import uz.uzkassa.horeca.enums.PaymentStatus;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 12.02.2024
 */

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailMobileDTO extends OrderGrandTotalDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5645633L;

    Long id;

    LocalDateTime orderDate;

    OrderStatus status;

    OrderReason reason;

    String orderNumber;

    PaymentStatus paymentStatus;

    Long numberOfGuest;

    CommonCodeDTO table;

    CommonDTO hall;

    String comment;

    BigDecimal totalSellingPrice;

    List<OrderItemDetailMobileDTO> items;

    List<BillListDTO> bills;

    CommonDTO receipt;

    CustomerDetailDTO client;

    CommonDTO waiter;

    CommonDTO updater;
}
