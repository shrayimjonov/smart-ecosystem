package uz.uzkassa.smartpos.integration.cto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CtoBranchSyncDTO implements Serializable {
    @NotNull
    private Long id;
    @NotBlank
    private String name;
    private Long activityTypeCode;
    private Long activityTypeParentCode;
    private CtoAddressDTO address;
    private String longitude;
    private String latitude;
    private Long ofdId;
    @NotNull
    private CtoCompanyDTO customer;
}
