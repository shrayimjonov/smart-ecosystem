package uz.uzkassa.horeca.dto.order.cabinet;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.horeca.dto.base.CommonCodeDTO;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.OrderStatus;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class OrderListCabinetDTO extends OrderGrandTotalDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3453453L;

    Long id;

    String orderNumber;

    LocalDateTime orderDate;

    LocalDateTime closedDate;

    CommonDTO client;

    OrderStatus status;

    CommonDTO waiter;

    Long numberOfGuest;

    CommonCodeDTO table;

    CommonDTO hall;

    CommonDTO shift;

    BigDecimal totalPrice;

    BigDecimal totalQty;

    BigDecimal totalVat;
}
