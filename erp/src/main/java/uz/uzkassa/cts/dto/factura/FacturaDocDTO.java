package uz.uzkassa.cts.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacturaDocDTO implements Serializable {
    @JsonProperty("FacturaNo")
    private String facturaNo;
    @JsonProperty("FacturaDate")
    private LocalDate facturaDate;
}
