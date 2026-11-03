package uz.uzkassa.smartpossupply.dto.planroute;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.customer.CustomerWithLimitDTO;
import uz.uzkassa.smartpossupply.dto.customerbranch.CustomerBranchAddressDTO;
import uz.uzkassa.smartpossupply.dto.equipment.EquipmentDetailDTO;
import uz.uzkassa.smartpossupply.enums.PlanRouteCustomerBranchStatus;
import uz.uzkassa.smartpossupply.enums.PlanRouteReason;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 02.02.2023 12:29
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRouteCustomerBranchDetailDTO {

    Long id;

    CustomerBranchAddressDTO customerBranch;

    CustomerWithLimitDTO customer;

    PlanRouteCustomerBranchStatus status;

    CommonDTO order;

    LocalDateTime startDate;

    LocalDateTime endDate;

    boolean unscheduled;

    boolean orderingBlocked;

    PlanRouteReason reason;

    List<EquipmentDetailDTO> equipments;
}
