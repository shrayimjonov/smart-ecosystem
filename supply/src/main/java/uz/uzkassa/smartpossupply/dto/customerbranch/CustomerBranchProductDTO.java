package uz.uzkassa.smartpossupply.dto.customerbranch;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 30.11.2023
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerBranchProductDTO {

    Long customerBranchId;

    Long planRouteCustomerBranchId;

    Set<CustomerBranchProductItemDTO> customerBranchProductItems = new LinkedHashSet<>();
}
