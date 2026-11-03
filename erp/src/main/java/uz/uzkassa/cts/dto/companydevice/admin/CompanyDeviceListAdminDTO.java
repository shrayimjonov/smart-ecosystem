package uz.uzkassa.cts.dto.companydevice.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.enums.DeviceStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyDeviceListAdminDTO implements Serializable {

    Long id;

    SerialNumberDTO device;

    DeviceStatus status;

    CompanyCommonDTO company;

    LocalDateTime createdDate;

    CommonDTO creator;
}
