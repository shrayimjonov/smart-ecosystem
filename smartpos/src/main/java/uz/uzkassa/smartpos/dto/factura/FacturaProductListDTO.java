package uz.uzkassa.smartpos.dto.factura;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    @JsonProperty("HasLgota")
    private Boolean hasLgota;
    @JsonProperty("HasMedical")
    private Boolean hasMedical;
    @JsonProperty("DeliverySumWithVat")
    private BigDecimal deliverySumWithVat;
    @JsonProperty("Products")
    private List<FacturaProductDTO> products = new ArrayList<>();
}
