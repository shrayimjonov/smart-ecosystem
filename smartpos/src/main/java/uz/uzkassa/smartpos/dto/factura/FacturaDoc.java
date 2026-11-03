package uz.uzkassa.smartpos.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FacturaDoc implements Serializable {
    @JsonProperty("FacturaNo")
    private String facturaNo;
    @JsonProperty("FacturaDate")
    private LocalDate facturaDate;
}
