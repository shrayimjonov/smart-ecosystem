package uz.uzkassa.cts.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.ActExistEnum;
import uz.uzkassa.cts.enums.CompanyType;
import uz.uzkassa.cts.enums.DeviceType;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractForLeasingFilter extends BaseFilter {

    ActExistEnum actExistEnum;
    CompanyType companyType;
    Long tariffId;
    DeviceType deviceType;
}
