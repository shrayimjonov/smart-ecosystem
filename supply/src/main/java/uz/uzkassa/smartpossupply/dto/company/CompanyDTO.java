package uz.uzkassa.smartpossupply.dto.company;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.activitytype.ActivityTypeDTO;
import uz.uzkassa.smartpossupply.dto.address.AddressDTO;
import uz.uzkassa.smartpossupply.enums.BusinessType;
import uz.uzkassa.smartpossupply.enums.CompanyType;
import uz.uzkassa.smartpossupply.enums.ProductGroup;
import uz.uzkassa.smartpossupply.enums.SourceType;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CompanyDTO extends CompanyBaseDTO {

    BusinessType businessType;

    AddressDTO address;

    ActivityTypeDTO activityType;

    CompanyType companyType;

    ProductGroup productGroup;

    String factoryId;

    String factoryCountry;

    String login;

    boolean isForeign;

    boolean hasContract;

    String director;

    String accountant;
}
