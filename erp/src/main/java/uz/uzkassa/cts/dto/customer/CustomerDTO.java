package uz.uzkassa.cts.dto.customer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.base.BaseAddressDTO;
import uz.uzkassa.cts.dto.base.FullNameDTO;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.dto.contact.ContactDTO;
import uz.uzkassa.cts.enums.BusinessType;
import uz.uzkassa.cts.enums.CustomerAppType;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerDTO extends CompanyCommonDTO implements Serializable {

    String companyName;

    FullNameDTO director;

    FullNameDTO accountant;

    String oked;

    Long activityTypeId;

    BusinessType businessType;

    BaseAddressDTO address;

    Long vatId;

    Long managerId;

    CustomerAppType appType;

    List<Long> branches;

    List<ContactDTO> phones;

    boolean fromQuickRegister;
}
