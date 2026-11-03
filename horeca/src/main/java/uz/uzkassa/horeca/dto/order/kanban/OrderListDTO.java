package uz.uzkassa.horeca.dto.order.kanban;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.OrderStatus;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 13.05.2024
 */
@Getter
@Setter
@NoArgsConstructor
public class OrderListDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 435158934663L;

    CommonDTO order;

    CommonDTO waiter;

    CommonDTO table;

    String comment;

    OrderStatus status;

    List<OrderItemListDTO> items = new ArrayList<>();

    public void addItem(OrderItemListDTO orderItem) {
        items.add(orderItem);
    }
}
