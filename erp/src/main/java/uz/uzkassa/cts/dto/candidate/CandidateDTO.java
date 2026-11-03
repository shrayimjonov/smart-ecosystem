package uz.uzkassa.cts.dto.candidate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.FullNameDTO;
import uz.uzkassa.cts.dto.contact.ContactDTO;
import uz.uzkassa.cts.dto.socialaddress.SocialAddressDTO;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CandidateDTO extends FullNameDTO implements Serializable {

    Long positionId;

    Long departmentId;

    Long vacancyId;

    List<ContactDTO> phones;

    List<ContactDTO> emails;

    List<SocialAddressDTO> socialAddresses;
}
