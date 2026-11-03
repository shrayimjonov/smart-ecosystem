package uz.uzkassa.cts.dto.candidate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.FullNameDTO;
import uz.uzkassa.cts.dto.contact.ContactListDTO;
import uz.uzkassa.cts.dto.socialaddress.SocialAddressListDTO;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CandidateDetailAdminDTO extends FullNameDTO implements Serializable {

    String candidateNumber;

    List<ContactListDTO> phones;

    List<ContactListDTO> emails;

    List<SocialAddressListDTO> socialAddresses;

    CommonDTO position;

    CommonDTO company;

    CommonDTO branch;

    CommonDTO department;

    CommonDTO vacancy;
}
