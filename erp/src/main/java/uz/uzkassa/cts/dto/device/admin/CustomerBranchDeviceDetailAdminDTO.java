package uz.uzkassa.cts.dto.device.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerBranchDeviceDetailAdminDTO extends RegistryDetailAdminDTO implements Serializable {

    CommonDTO customerBranch;
}
