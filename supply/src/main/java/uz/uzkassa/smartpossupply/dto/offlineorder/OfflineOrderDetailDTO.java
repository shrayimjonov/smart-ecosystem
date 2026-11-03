package uz.uzkassa.smartpossupply.dto.offlineorder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.OfflineOrderStatus;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OfflineOrderDetailDTO {

    Long id;

    Long orderId;

    OfflineOrderStatus status;

    String mobileId;

    String errorMessage;
}
