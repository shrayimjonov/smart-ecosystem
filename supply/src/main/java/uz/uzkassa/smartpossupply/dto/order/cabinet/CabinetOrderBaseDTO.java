package uz.uzkassa.smartpossupply.dto.order.cabinet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.order.Order;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.OrderStatus;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CabinetOrderBaseDTO extends CommonDTO {

    String orderNumber;

    OrderStatus status;

    public CabinetOrderBaseDTO(Long id, String name, String orderNumber, OrderStatus status) {
        super(id, name);
        this.orderNumber = orderNumber;
        this.status = status;
    }

    public static CabinetOrderBaseDTO getCabinetOrderDTO(Order order) {
        return new CabinetOrderBaseDTO(order.getId(), order.getName(), order.getOrderNumber(), order.getStatus());
    }
}