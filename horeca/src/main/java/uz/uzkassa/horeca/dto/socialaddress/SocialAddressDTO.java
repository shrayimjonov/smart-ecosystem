package uz.uzkassa.horeca.dto.socialaddress;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.enums.SocialAddressType;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SocialAddressDTO {

    Long id;

    Long companyId;

    SocialAddressType type;

    String uri;
}
