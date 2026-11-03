package uz.uzkassa.smartpossupply.dto.customer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.address.AddressDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.companybank.CompanyBankDTO;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerDTO extends CompanyBaseDTO {

    AddressDTO address;

    String contactPerson;

    String phone;

    CompanyBankDTO bank;

    Long companyId;

    Long activityTypeId;

    String director;

    String accountant;

    boolean syncBranches;

    boolean createDefault;

    String mobileId;

    String externalId;
}
