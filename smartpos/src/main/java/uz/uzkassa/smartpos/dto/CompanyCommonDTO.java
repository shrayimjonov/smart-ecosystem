package uz.uzkassa.smartpos.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpos.integration.supply.SupplierBranchWithAddressDTO;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyCommonDTO {
    String tin;
    String name;
    List<SupplierBranchWithAddressDTO> branches;
}
