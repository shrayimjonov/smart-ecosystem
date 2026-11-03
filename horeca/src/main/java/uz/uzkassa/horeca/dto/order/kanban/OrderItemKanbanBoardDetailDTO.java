package uz.uzkassa.horeca.dto.order.kanban;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.unit.MultiUnitListDTO;
import uz.uzkassa.horeca.enums.OrderItemStatus;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class OrderItemKanbanBoardDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 23322390999L;

    Long id;

    BigDecimal qty;

    String comment;

    OrderItemStatus status;

    LocalDateTime orderDate;

    CommonDTO unit;

    CommonDTO menuItem;

    MultiUnitListDTO multiUnit;

    LocalDateTime lastModifiedDate;

    boolean hasMark;

    CommonDTO chef;

    CommonDTO waiter;

    Set<String> marks;
}
