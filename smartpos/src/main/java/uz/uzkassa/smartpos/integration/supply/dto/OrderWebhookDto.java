package uz.uzkassa.smartpos.integration.supply.dto;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.integration.enums.OrderStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrderWebhookDto {
    private OrderStatus status;
    private LocalDateTime deliveryDate;
}
