package uz.uzkassa.horeca.dto.order.cabinet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonCodeDTO;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.reservation.CustomerDetailDTO;
import uz.uzkassa.horeca.dto.terminal.BillListDTO;
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
public class OrderDetailCabinetDTO extends OrderGrandTotalDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5645633L;

    Long id;

    String uid;

    CustomerDetailDTO client;

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

    CommonDTO receipt;

    CommonDTO shift;

    List<OrderItemDetailCabinetDTO> items;

    List<BillListDTO> bills;
}
