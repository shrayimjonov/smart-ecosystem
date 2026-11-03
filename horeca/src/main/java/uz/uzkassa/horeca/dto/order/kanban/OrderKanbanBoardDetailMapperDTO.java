package uz.uzkassa.horeca.dto.order.kanban;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.horeca.enums.OrderItemStatus;
import uz.uzkassa.horeca.enums.OrderStatus;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@SuppressWarnings("unused")
@Slf4j
public class OrderKanbanBoardDetailMapperDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 345466634L;

    Long id;
    String orderNumber;
    LocalDateTime orderDate;
    String comment;
    OrderStatus orderStatus;

    Long tableId;
    String tableName;
    LocalDateTime lastModifiedDate;

    Long hallId;
    String hallName;

    Long itemId;
    BigDecimal qty;
    String itemComment;
    OrderItemStatus status;
    LocalDateTime itemOrderDate;

    Long menuItemId;
    String menuItemName;

    Long multiUnitId;
    Double weight;
    BigDecimal price;
    Long unitId;
    String unitName;
    Long techCardId;
    String techCardName;
    boolean hasMark;
    Set<String> marks;
    Long chefId;
    String chefName;
    Long waiterId;
    String waiterName;

    public OrderKanbanBoardDetailMapperDTO(
            Long id,
            String orderNumber,
            LocalDateTime orderDate,
            String comment,
            String orderStatus,
            Long tableId,
            String tableName,
            Long hallId,
            String hallName,
            Long itemId,
            BigDecimal qty,
            String itemComment,
            String status,
            LocalDateTime itemOrderDate,
            LocalDateTime lastModifiedDate,
            String marks,
            Long chefId,
            String chefFirstName,
            String chefLastName,
            Long waiterId,
            String waiterFirstName,
            String waiterLastName,
            Long menuItemId,
            String menuItemName,
            Long multiUnitId,
            Double weight,
            BigDecimal price,
            Long unitId,
            String unitName,
            Long techCardId,
            String techCardName,
            Boolean hasMark
    ) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.comment = comment;
        this.orderStatus = OrderStatus.valueOf(orderStatus);
        this.tableId = tableId;
        this.tableName = tableName;
        this.hallId = hallId;
        this.hallName = hallName;
        this.itemId = itemId;
        this.qty = qty;
        this.itemComment = itemComment;
        this.status = OrderItemStatus.getByCode(status);
        this.itemOrderDate = itemOrderDate;
        this.lastModifiedDate = lastModifiedDate;
        this.menuItemId = menuItemId;
        this.menuItemName = menuItemName;
        this.multiUnitId = multiUnitId;
        this.weight = weight;
        this.price = price;
        this.unitId = unitId;
        this.unitName = unitName;
        this.techCardId = techCardId;
        this.techCardName = techCardName;
        this.hasMark = hasMark;
        this.chefId = chefId;
        if (chefId != null) {
            StringBuilder name = new StringBuilder();
            if (chefFirstName != null) {
                name.append(chefFirstName);
                name.append(" ");
            }
            if (chefLastName != null) {
                name.append(chefLastName);
            }
            this.chefName = name.toString().trim();
        }
        if (waiterId != null) {
            StringBuilder name = new StringBuilder();
            if (waiterFirstName != null) {
                name.append(waiterFirstName);
                name.append(" ");
            }
            if (waiterLastName != null) {
                name.append(waiterLastName);
            }
            this.waiterName = name.toString().trim();
        }
        this.waiterId = waiterId;
        if (StringUtils.isNotEmpty(marks)) {
            try {
                this.marks = new HashSet<>(new ObjectMapper().readValue(marks, new TypeReference<>() {
                }));
            } catch (JsonProcessingException e) {
                log.info("JsonProcessing error: {}", e.getMessage());
            }
        }
    }
}
