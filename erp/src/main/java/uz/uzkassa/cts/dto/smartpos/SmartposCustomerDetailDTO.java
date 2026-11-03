package uz.uzkassa.cts.dto.smartpos;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.user.UserShortDTO;
import uz.uzkassa.cts.enums.BusinessType;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class SmartposCustomerDetailDTO implements Serializable {

    Long id;

    String name;

    String tin;

    String oldTin;

    SmartposOwnerDTO owner;

    BigDecimal vat;

    BusinessType businessType;

    Long activityTypeCode;

    Long activityTypeParentCode;

    SmartPosAddressDTO address;

    String appType;

    UserShortDTO manager;

    public SmartposCustomerDetailDTO(Long id, String name, String tin, BusinessType businessType, BigDecimal vat, SmartPosAddressDTO address) {
        this.id = id;
        this.name = name;
        this.tin = tin;
        this.businessType = businessType;
        this.vat = vat;
        this.address = address;
    }
}
