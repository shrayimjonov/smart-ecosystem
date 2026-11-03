package uz.uzkassa.cts.dto.device.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.DeviceStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistryDetailAdminDTO implements Serializable {

    DeviceStatus status;

    CommonDTO creator;

    LocalDateTime createdDate;
}
