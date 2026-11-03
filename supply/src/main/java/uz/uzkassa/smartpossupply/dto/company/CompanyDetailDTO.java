package uz.uzkassa.smartpossupply.dto.company;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.activitytype.ActivityTypeDTO;
import uz.uzkassa.smartpossupply.dto.address.AddressDetailDTO;
import uz.uzkassa.smartpossupply.dto.bank.BankDetailDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.file.FileDTO;
import uz.uzkassa.smartpossupply.dto.user.OwnerDTO;
import uz.uzkassa.smartpossupply.enums.BusinessType;
import uz.uzkassa.smartpossupply.enums.CompanyStatus;
import uz.uzkassa.smartpossupply.enums.CompanyType;
import uz.uzkassa.smartpossupply.enums.ProductGroup;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CompanyDetailDTO extends CompanyBaseDTO {

    BusinessType businessType;

    AddressDetailDTO address;

    FileDTO logo;

    FileDTO backgroundImage;

    BankDetailDTO bank;

    ActivityTypeDTO activityType;

    ProductGroup productGroup;

    CompanyType companyType;

    OwnerDTO owner;

    CommonDTO manager;

    Long activeBranches;

    boolean freeTrial;

    LocalDateTime trialExpireDate;

    CompanyStatus status;

    boolean hasContract;

    String director;

    String accountant;
}
