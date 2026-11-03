package uz.uzkassa.cts.dto.company.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.base.BaseAddressDetailDTO;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.dto.contact.ContactListDTO;
import uz.uzkassa.cts.dto.socialaddress.SocialAddressListDTO;
import uz.uzkassa.cts.dto.vat.VatItemDTO;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CompanyDetailAdminDTO extends CompanyCommonDTO implements Serializable {

    String brand;

    String oked;

    VatItemDTO vat;

    List<ContactListDTO> phones;

    List<ContactListDTO> emails;

    List<SocialAddressListDTO> socialAddresses;
}
