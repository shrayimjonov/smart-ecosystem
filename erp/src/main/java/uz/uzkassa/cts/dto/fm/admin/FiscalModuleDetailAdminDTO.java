package uz.uzkassa.cts.dto.fm.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.dto.device.admin.*;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FiscalModuleDetailAdminDTO implements Serializable {

    SerialNumberDTO fiscalModule;

    RegistryDetailAdminDTO registryFiscalModule;

    CompanyDeviceDetailAdminDTO companyFiscalModule;

    BranchDeviceDetailAdminDTO branchFiscalModule;

    CustomerDeviceDetailAdminDTO customerFiscalModule;

    CustomerBranchDeviceDetailAdminDTO customerBranchFiscalModule;

    FiscalizationDetailAdminDTO fiscalization;
}
