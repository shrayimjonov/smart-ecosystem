package uz.uzkassa.cts.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class FacturaIncomingDto implements Serializable {
    @JsonProperty("errorCode")
    private Long errorCode;
    @JsonProperty("data")
    private FacturaIncomingDataDto data;
    @JsonProperty("status")
    private Long status;

}
