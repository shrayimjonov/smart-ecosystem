package uz.uzkassa.smartpossupply.dto.bankbranch;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.companybank.CompanyBankDetailDTO;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BranchBankDetailDTO extends CompanyBankDetailDTO {
    CommonDTO branch;
}
