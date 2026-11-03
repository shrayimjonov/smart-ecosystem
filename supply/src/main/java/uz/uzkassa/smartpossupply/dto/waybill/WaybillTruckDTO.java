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
public class WaybillTruckDTO implements Serializable {
    @JsonProperty("RegNo")
    private String regNo;
    @JsonProperty("Model")
    private String model;

    @JsonSetter("regno")
    public void setregnoToLowerCase(String regno) {
        this.regNo = regno;
    }

    @JsonSetter("model")
    public void setmodelToLowerCase(String model) {
        this.model = model;
    }

    public WaybillTruckDTO(String regNo, String model) {
        this.regNo = regNo;
        this.model = model;
    }
}
