package uz.uzkassa.cts.dto.customer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.base.BaseAddressDTO;
import uz.uzkassa.cts.dto.base.FullNameDTO;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerQuickDTO extends CompanyCommonDTO implements Serializable {

    String companyName;

    BaseAddressDTO address;

    String phone;

    FullNameDTO director;
}
