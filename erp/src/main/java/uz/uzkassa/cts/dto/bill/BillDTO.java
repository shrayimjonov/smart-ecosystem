package uz.uzkassa.cts.dto.bill;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.DeviceType;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@ToString
public class BillDTO implements Serializable {
    Long customerBranchId;
    Long branchId;
    Long customerId;
    Long deviceId;
    Long customerBranchDeviceId;
    Long paymentQuoteId;
    DeviceType deviceType;
}
