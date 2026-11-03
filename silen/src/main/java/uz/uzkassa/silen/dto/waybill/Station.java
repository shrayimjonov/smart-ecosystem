package uz.uzkassa.silen.dto.waybill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Railway.ProductGroup.LoadingPoint:
 *     {"StationId": "string", "StationName": "string"}
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Station implements Serializable {

    @JsonProperty("StationId")
    String stationId; //if TransportType.Railway
    @JsonProperty("StationName")
    String stationName; //if TransportType.Railway

    @JsonSetter("stationId")
    public void setStationIdLowerCase(String stationId) {
        this.stationId = stationId;
    }

    @JsonSetter("stationname")
    public void setStationNameLowerCase(String stationName) {
        this.stationName = stationName;
    }


}

