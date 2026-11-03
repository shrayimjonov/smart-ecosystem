package uz.uzkassa.silen.dto.invoice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
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

    @JsonSetter("facturano")
    public void setfacturano(String facturano) {
        this.facturaNo = facturano;
    }

    public void setFacturaNo(String facturaNo) {
        this.facturaNo = facturaNo;
    }

    @JsonSetter("facturadate")
    public void setfacturadate(LocalDate facturadate) {
        this.facturaDate = facturadate;
    }

    public void setFacturaDate(LocalDate facturaDate) {
        this.facturaDate = facturaDate;
    }
}
