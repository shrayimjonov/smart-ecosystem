package uz.uzkassa.cts.dto.customerdevice;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.branchdevice.BranchDeviceListAdminDTO;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerDeviceListAdminDTO extends BranchDeviceListAdminDTO {

    CommonDTO customer;
}
