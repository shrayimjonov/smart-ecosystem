package uz.uzkassa.smartpossupply.dto.waybill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class WaybillPointDTO implements Serializable {

    Long supplyRegionId;
    @JsonProperty("RegionId")
    Integer regionId;
    @JsonProperty("RegionName")
    String regionName;

    Long supplyDistrictId;
    @JsonProperty("DistrictCode")
    Integer districtCode;
    @JsonProperty("DistrictName")
    String districtName;

    @JsonProperty("MahallaId")
    Integer mahallaId;
    @JsonProperty("MahallaName")
    String mahallaName;
    @JsonProperty("Address")
    String address;
    @JsonProperty("Longitude")
    Double longitude;
    @JsonProperty("Latitude")
    Double latitude;
    @JsonSetter("regionid")
    public void setregionidToLowerCase(Integer regionId) {
        this.regionId = regionId;
    }

    @JsonSetter("regionname")
    public void setregionnameToLowerCase(String regionName) {
        this.regionName = regionName;
    }

    @JsonSetter("districtcode")
    public void setdistrictcodeToLowerCase(Integer districtCode) {
        this.districtCode = districtCode;
    }

    @JsonSetter("districtname")
    public void setdistrictnameToLowerCase(String districtName) {
        this.districtName = districtName;
    }

    @JsonSetter("mahallaname")
    public void setmahallanameToLowerCase(String mahallaName) {
        this.mahallaName = mahallaName;
    }

    @JsonSetter("address")
    public void setaddressToLowerCase(String address) {
        this.address = address;
    }

    @JsonSetter("mahallaid")
    public void setmahallaidToLowerCase(Integer mahallaId) {
        this.mahallaId = mahallaId;
    }

    @JsonSetter("longitude")
    public void setlongitudeToLowerCase(Double longitude) {
        this.longitude = longitude;
    }

    @JsonSetter("latitude")
    public void setlatitudeToLowerCase(Double latitude) {
        this.latitude = latitude;
    }
}

