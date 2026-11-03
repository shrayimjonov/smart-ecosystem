package uz.uzkassa.cts.dto.fm.admin;

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
@FieldDefaults(level = AccessLevel.PROTECTED)
public class CompanyFiscalModuleListAdminDTO implements Serializable {

    Long id;

    SerialNumberDTO fiscalModule;

    CompanyCommonDTO company;

    DeviceStatus status;

    LocalDateTime createdDate;

    CommonDTO creator;
}
