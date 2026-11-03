package uz.uzkassa.cts.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FacturaProductListDTO implements Serializable {
    @JsonProperty("FacturaProductId")
    private String facturaProductId;
    @JsonProperty("EmpowermentProductId")
    private String empowermentProductId;
    @JsonProperty("Tin")
    private String tin;
    @JsonProperty("HasVat")
    private Boolean hasVat;
    @JsonProperty("HasExcise")
    private Boolean hasExcise;
    @JsonProperty("HasComittent")
    private Boolean hasComittent;
    @JsonProperty("HasMedical")
    private Boolean hasMedical;
    @JsonProperty("DeliverySumWithVat")
    private BigDecimal deliverySumWithVat;
    @JsonProperty("Products")
    private List<FacturaProductDTO> products = new ArrayList<>();
}
