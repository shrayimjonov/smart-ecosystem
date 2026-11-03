package uz.uzkassa.cts.dto.device.admin;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.DeviceStatus;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class DeviceStatisticsAdminDTO {
    DeviceStatus status;
    Long total;
}
