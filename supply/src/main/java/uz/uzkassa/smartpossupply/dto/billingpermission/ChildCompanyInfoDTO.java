package uz.uzkassa.smartpossupply.dto.billingpermission;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChildCompanyInfoDTO {
    String id;
    String tin;
    String name;
}