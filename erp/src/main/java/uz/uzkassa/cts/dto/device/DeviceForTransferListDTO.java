package uz.uzkassa.cts.dto.device;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.DeviceStatus;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class DeviceForTransferListDTO implements Serializable {

    Long id;

    String serialNumber;

    Long deviceId;

    String model;

    DeviceStatus status;
}
