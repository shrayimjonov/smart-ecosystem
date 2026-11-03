package uz.uzkassa.smartpos.integration.cto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class CtoAddressDTO implements Serializable {
    private Long regionCode;
    private Long districtCode;
    private String street;
    private String house;
    private String apartment;

    @JsonIgnore
    public String getAddressStr() {
        StringBuilder addressString = new StringBuilder();
        if (StringUtils.isNotBlank(street)) {
            addressString.append(street);
        }
        if (StringUtils.isNotBlank(house)) {
            addressString.append(", ").append(house);
        }
        if (StringUtils.isNotBlank(apartment)) {
            addressString.append(", ").append(apartment);
        }
        return addressString.toString();
    }
}
