package uz.uzkassa.smartpos.dto.filter;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
public class CompanyFilter extends BaseFilter implements Serializable {
    Long regionId;
    Long districtId;
    Long activityTypeId;
    String businessType;
    Boolean freeTrial;
    String inn;
}
