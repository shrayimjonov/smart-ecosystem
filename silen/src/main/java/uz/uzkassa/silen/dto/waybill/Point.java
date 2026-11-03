package uz.uzkassa.silen.dto.waybill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Railway.ProductGroup.LoadingPoint:
 *     {"StationId": "string", "StationName": "string"}
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Point extends Station {

    @JsonProperty("RegionId")
    Integer regionId;
    @JsonProperty("RegionName")
    String regionName;

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
    public void setregionidLowerCase(Integer regionId) {
        this.regionId = regionId;
    }

    @JsonSetter("regionname")
    public void setregionnameLowerCase(String regionName) {
        this.regionName = regionName;
    }

    @JsonSetter("districtcode")
    public void setdistrictcodeLowerCase(Integer districtCode) {
        this.districtCode = districtCode;
    }

    @JsonSetter("districtname")
    public void setdistrictnameLowerCase(String districtName) {
        this.districtName = districtName;
    }

    @JsonSetter("mahallaname")
    public void setmahallanameLowerCase(String mahallaName) {
        this.mahallaName = mahallaName;
    }

    @JsonSetter("address")
    public void setaddressLowerCase(String address) {
        this.address = address;
    }

    @JsonSetter("mahallaid")
    public void setmahallaidLowerCase(Integer mahallaId) {
        this.mahallaId = mahallaId;
    }

    @JsonSetter("longitude")
    public void setlongitudeLowerCase(Double longitude) {
        this.longitude = longitude;
    }

    @JsonSetter("latitude")
    public void setlatitudeLowerCase(Double latitude) {
        this.latitude = latitude;
    }
}

