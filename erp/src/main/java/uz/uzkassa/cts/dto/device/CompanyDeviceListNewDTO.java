package uz.uzkassa.cts.dto.device;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.DevicePaymentType;
import uz.uzkassa.cts.enums.DeviceStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CompanyDeviceListNewDTO implements Serializable {

    Long id;

    String fiscalModuleSerial;

    String deviceSerial;

    Long customerId;

    String customer;

    String tin;

    String phone;

    Long customerBranchId;

    String customerBranch;

    LocalDateTime registrationDate;

    DeviceStatus status;

    DeviceStatus deviceStatus;

    String regionName;

    String districtName;

    DevicePaymentType paymentType;
}
