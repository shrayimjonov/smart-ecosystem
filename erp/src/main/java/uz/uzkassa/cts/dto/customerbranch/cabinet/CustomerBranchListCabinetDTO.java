package uz.uzkassa.cts.dto.customerbranch.cabinet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.base.AddressShortDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerBranchListCabinetDTO extends CommonDTO implements Serializable {

    String brand;

    AddressShortDTO address;

    CommonDTO activityType;

    CommonDTO apayCategory;

    String phone;
}
