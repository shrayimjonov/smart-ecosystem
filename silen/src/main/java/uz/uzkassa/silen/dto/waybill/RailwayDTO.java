package uz.uzkassa.silen.dto.waybill;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.silen.enumeration.TransportType;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RailwayDTO extends TransportDTO {

    @JsonProperty("TrainCarriageNumber")
    private String trainCarriageNumber;
    @JsonProperty("TrainDirection")
    private String trainDirection;

    private TransportType transportType = TransportType.Railway;
    @JsonSetter("traincarriagenumber")
    public void setTrainCarriageNumberLowerCase(String trainCarriageNumber) {
        this.trainCarriageNumber = trainCarriageNumber;
    }

    @JsonSetter("traindirection")
    public void setTrainDirectionLowerCase(String trainDirection) {
        this.trainDirection = trainDirection;
    }
}
