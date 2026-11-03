package uz.uzkassa.cts.dto.customer;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.base.BaseAddressDTO;
import uz.uzkassa.cts.dto.customerbranch.rent.CustomerBranchRentDTO;
import uz.uzkassa.cts.dto.socialaddress.SocialAddressDTO;
import uz.uzkassa.cts.enums.CustomerBranchApplicationStatus;
import uz.uzkassa.cts.enums.DeviceType;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CustomerBranchUpdateDTO implements Serializable {

    @NotNull
    Long id;

    String name;

    String brand;

    Long activityTypeId;

    Long apayCategoryId;

    String phone;

    BaseAddressDTO address;

    String cadastreNumber;

    CustomerBranchRentDTO rent;

    Long applicationId;

    CustomerBranchApplicationStatus applicationStatus;

    DeviceType deviceType;

    List<SocialAddressDTO> socialAddresses;
}
