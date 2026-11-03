package uz.uzkassa.cts.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class FacturaCheckStatusDto implements Serializable {
    @JsonProperty("errorCode")
    private Long errorCode;
    @JsonProperty("data")
    private FacturaCheckStatusDataDto data;
    @JsonProperty("status")
    private Long status;

}

//{"errorCode":0,"data":{"status":"ACCEPTED","facturaId":"605251b9e83b470001d99378"},"status":0}
