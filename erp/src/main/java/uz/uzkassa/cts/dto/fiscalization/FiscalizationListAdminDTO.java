package uz.uzkassa.cts.dto.fiscalization;

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
public class FiscalizationListAdminDTO implements Serializable {

    Long id;

    SerialNumberDTO device;

    SerialNumberDTO fiscalModule;

    CompanyCommonDTO company;

    CommonDTO branch;

    CompanyCommonDTO customer;

    CommonDTO customerBranch;

    DeviceStatus status;

    LocalDateTime createdDate;

    CommonDTO creator;
}
