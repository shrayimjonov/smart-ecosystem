package uz.uzkassa.cts.dto.branch.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.base.BaseAddressDetailDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.enums.CommonStatus;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BranchDetailAdminDTO extends CommonDTO implements Serializable {

    CommonDTO parent;

    BaseAddressDetailDTO address;

    CompanyCommonDTO company;

    CommonStatus status;
}
