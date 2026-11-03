package uz.uzkassa.cts.dto.device;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.DeviceStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class BaseDeviceFiscalModuleListDTO extends BaseDeviceFiscalModuleDTO {

    Long id;

    String deviceSerial;

    String fiscalModuleSerial;

    LocalDateTime registrationDate;

    DeviceStatus status;

    Long customerBranchId;

    String customerBranch;

    Long customerId;

    String customer;

    String tin;

    String creatorId;

    String creatorName;

    String ofdStatus;

}
