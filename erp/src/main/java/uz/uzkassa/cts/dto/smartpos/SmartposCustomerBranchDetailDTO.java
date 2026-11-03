package uz.uzkassa.cts.dto.smartpos;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.BusinessType;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class SmartposCustomerBranchDetailDTO implements Serializable {

    Long id;

    String name;

    BusinessType businessType;

    Long activityTypeCode;

    Long activityTypeParentCode;

    SmartPosAddressDTO address;

    String longitude;

    String latitude;

    Long ofdId;

    public SmartposCustomerBranchDetailDTO(Long id, String name, BusinessType businessType, SmartPosAddressDTO address, String longitude, String latitude) {
        this.id = id;
        this.name = name;
        this.businessType = businessType;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
