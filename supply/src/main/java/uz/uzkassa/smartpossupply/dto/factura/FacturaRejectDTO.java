package uz.uzkassa.smartpossupply.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class FacturaRejectDTO implements Serializable {

    @JsonProperty("Factura")
    private FacturaDTO factura;

    @JsonProperty("Notes")
    private String notes;
}
