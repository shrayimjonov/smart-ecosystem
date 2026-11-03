package uz.uzkassa.smartpos.dto.nic;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpos.dto.factura.FacturaDTO;

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
