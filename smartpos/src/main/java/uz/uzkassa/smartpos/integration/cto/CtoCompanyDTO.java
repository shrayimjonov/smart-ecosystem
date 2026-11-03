package uz.uzkassa.smartpos.integration.cto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.CommonUserDTO;
import uz.uzkassa.smartpos.dto.FullNameDTO;
import uz.uzkassa.smartpos.enums.BillingType;
import uz.uzkassa.smartpos.enums.BusinessType;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class CtoCompanyDTO implements Serializable {
    @NotNull
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String tin;
    private String oldTin;
    private BigDecimal vat;
    private CtoAddressDTO address;
    private BusinessType businessType;
    private Long activityTypeCode;
    private Long activityTypeParentCode;
    @NotNull
    private FullNameDTO owner;
    private BillingType appType;
    private CommonUserDTO manager;
}
