package uz.uzkassa.horeca.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FacturaSendDTO implements Serializable {

    @JsonProperty("Sign")
    private String sign;

    @JsonProperty("ClientIp")
    private String clientIp;

    @JsonProperty("FacturaId")
    private String facturaId;

    @JsonProperty("Action")
    private String action;

    @JsonProperty("Notes")
    private String notes;

    private String locale;
    private String tin;
    private Long id;
}
