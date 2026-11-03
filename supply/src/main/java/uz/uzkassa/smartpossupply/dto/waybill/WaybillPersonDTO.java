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
public class WaybillPersonDTO implements Serializable {

    private Long id;
    @JsonProperty("Pinfl")
    private String pinfl;

    @JsonProperty("FullName")
    private String fullName;

    @JsonSetter("pinfl")
    public void setpinflToLowerCase(String pinfl) {
        this.pinfl = pinfl;
    }

    @JsonSetter("fullname")
    public void setfullnameToLowerCase(String fullname) {
        this.fullName = fullname;
    }


    public WaybillPersonDTO(String pinfl, String fullName) {
        this.pinfl = pinfl;
        this.fullName = fullName;
    }
}
