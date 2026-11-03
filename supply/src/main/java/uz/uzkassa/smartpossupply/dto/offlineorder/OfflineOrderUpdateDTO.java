package uz.uzkassa.smartpossupply.dto.offlineorder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.OfflineOrderStatus;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OfflineOrderUpdateDTO {

    Long id;

    Long orderId;

    OfflineOrderStatus status;

    String errorMessage;

    public OfflineOrderUpdateDTO(Long id, Long orderId, OfflineOrderStatus status, String errorMessage) {
        this.id = id;
        this.orderId = orderId;
        this.status = status;
        this.errorMessage = errorMessage;
    }
}


