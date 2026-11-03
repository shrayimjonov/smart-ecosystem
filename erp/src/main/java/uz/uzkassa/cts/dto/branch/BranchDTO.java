package uz.uzkassa.cts.dto.branch;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.base.BaseAddressDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BranchDTO extends CommonDTO implements Serializable {

    Long parentId;

    Long companyId;

    BaseAddressDTO address;
}
