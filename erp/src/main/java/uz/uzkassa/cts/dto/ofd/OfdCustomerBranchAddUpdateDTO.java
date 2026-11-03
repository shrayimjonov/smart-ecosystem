package uz.uzkassa.cts.dto.ofd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OfdCustomerBranchAddUpdateDTO implements Serializable {

    private Long id;
    private String tin;
    private String pinfl;
    private String name;
    private String cadastreNumber;
    private Long regionCode;
    private Long districtCode;
    private String address;
    private String longitude;
    private String latitude;
    private String rentNumber;
    private String rentBeginDate;
    private String rentEndDate;
    private Long activityGroup;
    private Long activityType;
    private Integer neighborhoodCode;
    private List<OfdFileDTO> files;

    @Override
    public String toString() {
        return "OfdCustomerBranchAddUpdateDTO{" +
                "id=" + id +
                ", tin='" + tin + '\'' +
                ", pinfl='" + pinfl + '\'' +
                ", name='" + name + '\'' +
                ", cadastreNumber='" + cadastreNumber + '\'' +
                ", regionCode=" + regionCode +
                ", districtCode=" + districtCode +
                ", address='" + address + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", rentNumber='" + rentNumber + '\'' +
                ", rentBeginDate='" + rentBeginDate + '\'' +
                ", rentEndDate='" + rentEndDate + '\'' +
                ", activityGroup=" + activityGroup +
                ", activityType=" + activityType +
                ", neighborhoodCode=" + neighborhoodCode +
                ", files=" + files +
                '}';
    }
}
