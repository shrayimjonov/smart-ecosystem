package uz.uzkassa.smartpossupply.dto.planroute.dashboard;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRouteDashboardCustomerDetailDTO implements Serializable {

    PlanRouteDashboardCustomerDTO customer;

    PlanRouteDashboardCustomerBranchDTO customerBranch;
}
