package uz.uzkassa.cts.dto.customerbranchdevice;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.customerdevice.CustomerDeviceListAdminDTO;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerBranchDeviceListAdminDTO extends CustomerDeviceListAdminDTO {

    CommonDTO customerBranch;
}
