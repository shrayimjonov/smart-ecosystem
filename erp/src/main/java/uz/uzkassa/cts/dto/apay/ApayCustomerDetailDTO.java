package uz.uzkassa.cts.dto.apay;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.cts.enums.BusinessType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class ApayCustomerDetailDTO implements Serializable {

    Long id;

    String name;

    String companyName;

    String tin;

    String phone;

    ApayActivityTypeDTO activityType;

    BusinessType businessType;

    ApayAddressDTO address;

    DirectorDTO owner;

    ApayBankDTO bank;

    boolean fromCts;

    boolean fromNic;

    BigDecimal vatRate;

    List<ApayBranchDTO> branchList = new ArrayList<>();

    @JsonIgnore
    public void addBranch(ApayBranchDTO apayBranchDTO) {
        branchList.add(apayBranchDTO);
    }

}
