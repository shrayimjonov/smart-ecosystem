package uz.uzkassa.smartpossupply.dto.planroute;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.customerbranch.CustomerBranchLookUpDTO;
import uz.uzkassa.smartpossupply.dto.order.mobile.MobileReturnOrderDTO;
import uz.uzkassa.smartpossupply.enums.PlanRouteCustomerBranchStatus;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRouteCustomerBranchInfoDTO {
    Long planRouteCustomerBranchId;

    CommonDTO customer;

    CustomerBranchLookUpDTO customerBranch;

    PlanRouteCustomerBranchStatus status;

    MobileReturnOrderDTO order;
}
