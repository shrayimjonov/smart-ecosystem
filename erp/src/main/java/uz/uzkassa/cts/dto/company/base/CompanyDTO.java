package uz.uzkassa.cts.dto.company.base;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.AddressDTO;
import uz.uzkassa.cts.dto.address.base.BaseAddressDTO;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.dto.contact.ContactDTO;
import uz.uzkassa.cts.dto.socialaddress.SocialAddressDTO;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyDTO extends CompanyCommonDTO implements Serializable {

    String brand;

    String oked;

    Long vatId;

    List<ContactDTO> phones;

    List<ContactDTO> emails;

    BaseAddressDTO address;

    List<AddressDTO> addresses;

    List<SocialAddressDTO> socialAddresses;
}
