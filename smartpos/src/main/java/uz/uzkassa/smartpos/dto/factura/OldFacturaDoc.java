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
public class OldFacturaDoc implements Serializable {
    @JsonProperty("OldFacturaId")
    private String oldFacturaId;
    @JsonProperty("OldFacturaNo")
    private String oldFacturaNo;
    @JsonProperty("OldFacturaDate")
    private LocalDate oldFacturaDate;
}
