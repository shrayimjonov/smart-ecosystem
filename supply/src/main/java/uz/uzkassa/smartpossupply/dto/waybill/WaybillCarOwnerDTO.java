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
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WaybillCarOwnerDTO implements Serializable {

    @JsonProperty("TinOrPinfl")
    private String tinOrPinfl;

    @JsonProperty("Name")
    private String name;

    @JsonSetter("TinOrPinfl")
    public void settinorpinfl(String tinOrPinfl) {
        this.tinOrPinfl = tinOrPinfl;
    }

    @JsonSetter("Name")
    public void setname(String name) {
        this.name = name;
    }

    public WaybillCarOwnerDTO(String tinOrPinfl, String name) {
        this.tinOrPinfl = tinOrPinfl;
        this.name = name;
    }
}
