package uz.uzkassa.smartpossupply.dto.waybill;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpossupply.enums.TrailerType;
import uz.uzkassa.smartpossupply.enums.TransportType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WaybillRoadWayTransportDTO implements Serializable {
    private Long id;
    @JsonProperty("Driver")
    private WaybillPersonDTO driver;

    @JsonProperty("Truck")
    private WaybillTruckDTO truckDTO;

    @JsonProperty("Trailer")
    private WaybillTruckDTO trailer;

    @JsonProperty("Carriages")
    private List<WaybillTruckDTO> carriages;

    @JsonProperty("OtherCarOwners")
    private List<WaybillCarOwnerDTO> otherCarOwners;

    //for rail-way
    @JsonProperty("TrainCarriageNumber")
    private String trainCarriageNumber;

    @JsonProperty("TrainDirection")
    private String trainDirection;

    @JsonProperty("ProductGroups")
    private List<WaybillProductGroupDTO> productGroups;

    private TrailerType trailerType;
    private TransportType transportType;

    @JsonSetter("driver")
    public void setdriverToLowerCase(WaybillPersonDTO driver) {
        this.driver = driver;
    }

    @JsonSetter("truck")
    public void settruckToLowerCase(WaybillTruckDTO truckDTO) {
        this.truckDTO = truckDTO;
    }

    @JsonSetter("trailer")
    public void settrailerToLowerCase(WaybillTruckDTO trailer) {
        this.trailer = trailer;
    }

    @JsonSetter("carriages")
    public void setcarriagesToLowerCase(List<WaybillTruckDTO> carriages) {
        this.carriages = carriages;
    }

    @JsonSetter("productgroups")
    public void setproductgroupsToLowerCase(List<WaybillProductGroupDTO> productgroups) {
        this.productGroups = productgroups;
    }

    @JsonSetter("othercarowners")
    public void setothercarownersToLowerCase(List<WaybillCarOwnerDTO> otherCarOwners) {
        this.otherCarOwners = otherCarOwners;
    }

    @JsonIgnore
    public void addProductGroup(WaybillProductGroupDTO productGroupDTO) {
        if (this.productGroups == null) {
            this.productGroups = new ArrayList<>();
        }
        productGroups.add(productGroupDTO);
    }
}
