package uz.uzkassa.smartpossupply.dto.customer.mobile;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerListMobileDTO extends CompanyBaseDTO {

    String contactPerson;

    String phone;

    Integer customerBranchCount;
}
