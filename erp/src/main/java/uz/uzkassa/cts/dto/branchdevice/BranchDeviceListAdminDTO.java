package uz.uzkassa.cts.dto.branchdevice;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.companydevice.admin.CompanyDeviceListAdminDTO;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BranchDeviceListAdminDTO extends CompanyDeviceListAdminDTO {

    CommonDTO branch;
}
