package uz.uzkassa.cts.dto.customer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.base.BaseAddressDetailDTO;
import uz.uzkassa.cts.dto.apay.category.APayCategoryDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.customerbranch.rent.CustomerBranchRentDTO;
import uz.uzkassa.cts.dto.socialaddress.SocialAddressListDTO;
import uz.uzkassa.cts.enums.OfdStatus;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CustomerBranchDetailDTO implements Serializable {

    Long id;

    String name;

    String brand;

    CustomerShortDTO customer;

    CommonDTO activityType;

    CommonDTO parentActivityType;

    APayCategoryDTO apayCategory;

    APayCategoryDTO parentApayCategory;

    String phone;

    BaseAddressDetailDTO address;

    String cadastreNumber;

    CustomerBranchRentDTO rent;

    int deviceTotal;

    int fiscalModuleTotal;

    Long ofdId;

    OfdStatus ofdStatus;

    boolean sync;

    boolean reSync;

    List<SocialAddressListDTO> socialAddresses;
}
