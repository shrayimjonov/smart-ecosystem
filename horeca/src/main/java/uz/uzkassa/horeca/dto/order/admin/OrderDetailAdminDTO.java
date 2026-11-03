package uz.uzkassa.horeca.dto.order.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonCodeDTO;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.order.cabinet.OrderGrandTotalDTO;
import uz.uzkassa.horeca.enums.OrderStatus;
import uz.uzkassa.horeca.enums.PaymentStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailAdminDTO extends OrderGrandTotalDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5645633L;

    Long id;

    CommonDTO client;

    CommonDTO waiter;

    CommonDTO branch;

    LocalDateTime orderDate;

    OrderStatus status;

    String orderNumber;

    PaymentStatus paymentStatus;

    String comment;

    Long numberOfGuest;

    CommonCodeDTO table;

    CommonDTO hall;

    List<OrderItemDetailAdminDTO> items;
}
