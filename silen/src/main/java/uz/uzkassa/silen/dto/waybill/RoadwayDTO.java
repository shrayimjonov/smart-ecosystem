package uz.uzkassa.silen.dto.waybill;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.silen.enumeration.TrailerType;
import uz.uzkassa.silen.enumeration.TransportType;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoadwayDTO extends TransportDTO {

    @JsonProperty("OtherCarOwners")
    private List<OtherCarOwner> otherCarOwners;

    @JsonProperty("Driver")
    private PersonDTO driver;

    @JsonProperty("Truck")
    private TruckDTO truckDTO;

    @JsonProperty("Trailer")
    private TruckDTO trailer;

    @JsonProperty("Carriages")
    private List<TruckDTO> carriages;

    private TrailerType trailerType;
    private TransportType transportType = TransportType.Roadway;

    @JsonSetter("driver")
    public void setdriverLowerCase(PersonDTO driver) {
        this.driver = driver;
    }

    @JsonSetter("truck")
    public void settruckLowerCase(TruckDTO truckDTO) {
        this.truckDTO = truckDTO;
    }

    @JsonSetter("trailer")
    public void settrailerLowerCase(TruckDTO trailer) {
        this.trailer = trailer;
    }

    @JsonSetter("carriages")
    public void setcarriagesLowerCase(List<TruckDTO> carriages) {
        this.carriages = carriages;
    }

    @JsonSetter("othercarowners")
    public void setothercarownersLowerCase(List<OtherCarOwner> otherCarOwners) {
        this.otherCarOwners = otherCarOwners;
    }
}
