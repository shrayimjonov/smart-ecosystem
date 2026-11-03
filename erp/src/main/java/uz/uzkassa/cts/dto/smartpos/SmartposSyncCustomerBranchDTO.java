package uz.uzkassa.cts.dto.smartpos;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@ToString
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class SmartposSyncCustomerBranchDTO implements Serializable {

    private Long id;
    private String name;
    private Long activityTypeCode;
    private Long activityTypeParentCode;
    private SmartPosAddressDTO address;
    private String longitude;
    private String latitude;
    private Long ofdId;
    private SmartposCustomerDetailDTO customer;
}
