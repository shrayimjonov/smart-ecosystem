package uz.uzkassa.cts.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.ActDocFlowStatus;
import uz.uzkassa.cts.enums.CompanyType;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActOfTransmissionFilter extends BaseFilter {
    ActDocFlowStatus actDocFlowStatus;
    CompanyType companyType;
    Long tariffId;
}
