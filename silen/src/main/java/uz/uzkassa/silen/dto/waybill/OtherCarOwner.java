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
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OtherCarOwner implements Serializable {

    @EqualsAndHashCode.Include
    @JsonProperty("TinOrPinfl")
    private String tinOrPinfl;

    @JsonProperty("Name")
    private String name;

    @JsonSetter("tinorpinfl")
    public void settinorpinflLowerCase(String tinOrPinfl) {
        this.tinOrPinfl = tinOrPinfl;
    }

    @JsonSetter("name")
    public void setnameLowerCase(String name) {
        this.name = name;
    }

}
