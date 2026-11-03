package uz.uzkassa.horeca.dto.order.mobile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.horeca.dto.base.CommonCodeDTO;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.order.cabinet.OrderGrandTotalDTO;
import uz.uzkassa.horeca.enums.OrderStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderListMobileDTO extends OrderGrandTotalDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3453453L;

    Long id;

    String orderNumber;

    LocalDateTime orderDate;

    CommonDTO client;

    OrderStatus status;

    CommonDTO waiter;

    Long numberOfGuest;

    CommonCodeDTO table;

    CommonDTO hall;

    List<OrderItemDetailMobileDTO> items;

    Integer readyCount;

    Integer itemCount;
}
