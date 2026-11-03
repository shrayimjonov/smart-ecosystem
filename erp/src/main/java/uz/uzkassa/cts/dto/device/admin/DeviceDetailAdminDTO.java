package uz.uzkassa.cts.dto.device.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceDetailAdminDTO implements Serializable {

    SerialNumberDTO device;

    RegistryDetailAdminDTO registryDevice;

    CompanyDeviceDetailAdminDTO companyDevice;

    BranchDeviceDetailAdminDTO branchDevice;

    CustomerDeviceDetailAdminDTO customerDevice;

    CustomerBranchDeviceDetailAdminDTO customerBranchDevice;

    FiscalizationDetailAdminDTO fiscalization;
}
