package uz.uzkassa.cts.dto.customer;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.base.BaseAddressDTO;
import uz.uzkassa.cts.dto.customerbranch.rent.CustomerBranchRentDTO;
import uz.uzkassa.cts.dto.socialaddress.SocialAddressDTO;
import uz.uzkassa.cts.enums.DeviceType;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CustomerBranchOldDTO implements Serializable {

    Long id;

    @NotNull
    Long customerId;

    @NotNull
    Long activityTypeId;

    Long apayCategoryId;

    @NotNull
    @NotEmpty
    String name;

    String brand;

    String phone;

    BaseAddressDTO address;

    String cadastreNumber;

    CustomerBranchRentDTO rent;

    List<SocialAddressDTO> socialAddresses;

    DeviceType deviceType;
}
