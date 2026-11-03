package uz.uzkassa.smartpossupply.dto.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpossupply.enums.OrderRejectReason;
import uz.uzkassa.smartpossupply.enums.OrderStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class OrderStatusUpdateDTO implements Serializable {

    OrderStatus status;
    LocalDateTime lastModifiedDate;
    Long expeditorId;
    OrderRejectReason rejectReason;

    public OrderStatusUpdateDTO(OrderStatus orderStatus, LocalDateTime updatedDate) {
        this.status = orderStatus;
        this.lastModifiedDate = updatedDate;
    }

    public OrderStatusUpdateDTO(OrderStatus orderStatus, LocalDateTime updatedDate, Long expeditorId) {
        this.status = orderStatus;
        this.lastModifiedDate = updatedDate;
        this.expeditorId = expeditorId;
    }

    public OrderStatusUpdateDTO(OrderStatus orderStatus, LocalDateTime updatedDate, OrderRejectReason rejectReason) {
        this.status = orderStatus;
        this.lastModifiedDate = updatedDate;
        this.rejectReason = rejectReason;
    }
}
