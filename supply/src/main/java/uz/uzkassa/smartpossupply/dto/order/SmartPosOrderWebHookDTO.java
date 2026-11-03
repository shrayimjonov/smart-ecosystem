package uz.uzkassa.smartpossupply.dto.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.OrderStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 14.11.2023
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SmartPosOrderWebHookDTO implements Serializable {

    LocalDateTime deliveryDate;

    OrderStatus status;

    public SmartPosOrderWebHookDTO(OrderStatus status) {
        this.status = status;
    }
}
