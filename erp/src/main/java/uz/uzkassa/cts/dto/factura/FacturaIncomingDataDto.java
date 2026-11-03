package uz.uzkassa.cts.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class FacturaIncomingDataDto implements Serializable {
    @JsonProperty("items")
    private List<InvoiceDTO> items;
    @JsonProperty("total")
    private Long total;
}
