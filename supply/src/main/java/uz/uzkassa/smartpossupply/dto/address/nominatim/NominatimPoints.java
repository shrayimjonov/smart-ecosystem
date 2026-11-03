package uz.uzkassa.smartpossupply.dto.address.nominatim;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NominatimPoints {

    String longitude;

    String latitude;

    public NominatimPoints(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
