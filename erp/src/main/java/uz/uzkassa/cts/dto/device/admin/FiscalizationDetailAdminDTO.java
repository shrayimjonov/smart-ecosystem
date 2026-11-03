package uz.uzkassa.cts.dto.device.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FiscalizationDetailAdminDTO extends RegistryDetailAdminDTO implements Serializable {

    CompanyCommonDTO company;

    CommonDTO branch;

    CompanyCommonDTO customer;

    CommonDTO customerBranch;

    SerialNumberDTO fiscalModule;

    SerialNumberDTO device;
}
