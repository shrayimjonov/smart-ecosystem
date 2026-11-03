package uz.uzkassa.silen.dto.waybill;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonDTO implements Serializable {

    @JsonProperty("Pinfl")
    private String pinfl;

    @JsonProperty("FullName")
    private String fullName;

    @JsonSetter("pinfl")
    public void setpinflLowerCase(String pinfl) {
        this.pinfl = pinfl;
    }

    @JsonSetter("fullname")
    public void setfullnameLowerCase(String fullname) {
        this.fullName = fullname;
    }
}
