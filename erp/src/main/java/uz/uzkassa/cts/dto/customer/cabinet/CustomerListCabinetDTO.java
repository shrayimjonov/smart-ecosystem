package uz.uzkassa.cts.dto.customer.cabinet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.base.AddressShortDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.enums.BusinessType;
import uz.uzkassa.cts.enums.CustomerStatus;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerListCabinetDTO extends CompanyCommonDTO implements Serializable {

    String companyName;

    CommonDTO activityType;

    BusinessType businessType;

    String phone;

    AddressShortDTO address;

    CustomerStatus status;
}
