package uz.uzkassa.cts.dto.warehouse;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.base.BaseAddressDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class WarehouseDTO extends CommonDTO implements Serializable {

    Long managerId;

    Long branchId;

    Long departmentId;

    BaseAddressDTO address;
}
