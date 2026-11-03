package uz.uzkassa.cts.dto.customerbranch.cabinet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.base.BaseAddressDetailDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.contact.ContactListDTO;
import uz.uzkassa.cts.dto.socialaddress.SocialAddressListDTO;
import uz.uzkassa.cts.enums.CustomerBranchStatus;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerBranchDetailCabinetDTO extends CommonDTO implements Serializable {

    String brand;

    BaseAddressDetailDTO address;

    CommonDTO activityType;

    CommonDTO apayCategory;

    String cadastreNumber;

    List<ContactListDTO> phones;

    List<SocialAddressListDTO> socialAddresses;

    CustomerBranchStatus status;

    String reason;
}
