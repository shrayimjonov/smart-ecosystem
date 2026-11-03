package uz.uzkassa.horeca.dto.cts;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CtsFiscalizationCheckDeviceDTO implements Serializable {

    Long branchId;

    Long customerId;

    Long customerBranchId;

    String deviceSerial;

    Long deviceModelId;

    LocalDateTime fiscalizationDate;

    String fiscalModuleSerial;

    CtsDeviceStatusEnum status;

    CtsOfdStatusEnum ofdStatus;

    boolean sendGeolocation;
}
