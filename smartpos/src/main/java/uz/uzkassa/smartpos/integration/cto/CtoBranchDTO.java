package uz.uzkassa.smartpos.integration.cto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CtoBranchDTO implements Serializable {
    private Long id;
    private String name;
    private Long activityTypeCode;
    private Long activityTypeParentCode;
    private CtoAddressDTO address;
    private String longitude;
    private String latitude;
    private Long ofdId;
}
