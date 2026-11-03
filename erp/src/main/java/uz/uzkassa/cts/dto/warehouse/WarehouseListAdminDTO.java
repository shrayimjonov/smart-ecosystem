package uz.uzkassa.cts.dto.warehouse;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.base.AddressShortDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.CommonStatus;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class WarehouseListAdminDTO extends CommonDTO implements Serializable {

    CommonStatus status;

    CommonDTO company;

    CommonDTO branch;

    CommonDTO department;

    CommonDTO manager;

    AddressShortDTO address;
}
