package uz.uzkassa.smartpossupply.dto.planroutetemplate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.customerbranch.CustomerBranchAddressDTO;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRouteTemplateCustomerBranchDetailDTO {

    Long id;

    CustomerBranchAddressDTO customerBranch;
}
