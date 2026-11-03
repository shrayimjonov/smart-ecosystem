package uz.uzkassa.silen.dto.waybill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.*;

import java.io.Serializable;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TruckDTO implements Serializable {

    @EqualsAndHashCode.Include
    @JsonProperty("RegNo")
    private String regNo;
    @JsonProperty("Model")
    private String model;

    @JsonSetter("regno")
    public void setregnoLowerCase(String regno) {
        this.regNo = regno;
    }

    @JsonSetter("model")
    public void setmodelLowerCase(String model) {
        this.model = model;
    }

}
