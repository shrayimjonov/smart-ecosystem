package uz.uzkassa.smartpos.integration.ofd;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
public class OfdCustomerBranchAddUpdateDTO implements Serializable {
    private Long id;
    private String tin;
    private String pinfl;
    private String name;
    private String cadastreNumber;
    private Long regionCode;
    private Long districtCode;
    private Long neighborhoodCode;
    private String address;
    private String longitude;
    private String latitude;
    private String rentNumber;
    private Long activityGroup;
    private Long activityType;
    private List<OfdFileDTO> files;
}
