package uz.uzkassa.horeca.dto.order.kanban;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.horeca.dto.base.CommonDTO;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@SuppressWarnings("unused")
public class OrderKanbanBoardListDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 43534663L;

    Long id;

    String orderNumber;

    LocalDateTime orderDate;

    CommonDTO table;

    CommonDTO hall;

    BigDecimal totalCount;

    BigDecimal completedCount;

    public OrderKanbanBoardListDTO(
        Long id,
        String orderNumber,
        LocalDateTime orderDate,
        Long hallId,
        String hallName,
        Long tableId,
        String tableName,
        BigDecimal totalCount,
        BigDecimal completedCount
    ) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.table = new CommonDTO(tableId, tableName);
        this.hall=new CommonDTO(hallId,hallName);
        this.totalCount = totalCount;
        this.completedCount = completedCount;
    }
}
