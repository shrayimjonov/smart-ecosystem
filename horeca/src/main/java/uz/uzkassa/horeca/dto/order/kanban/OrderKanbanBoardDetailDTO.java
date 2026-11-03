package uz.uzkassa.horeca.dto.order.kanban;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.OrderStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderKanbanBoardDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 989123485934L;

    Long id;

    String orderNumber;

    LocalDateTime orderDate;

    String comment;

    CommonDTO table;

    CommonDTO hall;

    OrderStatus status;

    List<OrderItemKanbanBoardDetailDTO> items = new ArrayList<>();

    @JsonIgnore
    public void addItem(OrderItemKanbanBoardDetailDTO item) {
        items.add(item);
    }
}
