package uz.uzkassa.cts.dto.customerbranch;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.base.BaseAddressDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.contact.ContactDTO;
import uz.uzkassa.cts.dto.socialaddress.SocialAddressDTO;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerBranchDTO extends CommonDTO implements Serializable {

    String brand;

    Long customerId;

    BaseAddressDTO address;

    Long branchId;

    Long activityTypeId;

    Long apayCategoryId;

    String cadastreNumber;

    List<ContactDTO> phones;

    List<SocialAddressDTO> socialAddresses;

    boolean fromQuickRegister;
}
