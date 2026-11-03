package uz.uzkassa.silen.dto.waybill;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.silen.enumeration.TransportType;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WaybillRouteDTO {
    String id;

    @NotNull
    String tin;

    TransportType transportType = TransportType.Roadway;

    @NotNull
    Integer regionId;
    @NotNull
    String regionName;

    @NotNull
    Integer districtCode;
    @NotNull
    String districtName;

    Integer mahallaId;
    String mahallaName;
    String address;
    Double longitude;
    Double latitude;

}
