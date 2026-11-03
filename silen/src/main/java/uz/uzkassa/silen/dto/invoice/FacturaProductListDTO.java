package uz.uzkassa.silen.dto.invoice;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
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

    @JsonSetter("facturaproductid")
    public void setfacturaproductid(String facturaproductid) {
        this.facturaProductId = facturaproductid;
    }

    public void setFacturaProductId(String facturaProductId) {
        this.facturaProductId = facturaProductId;
    }

    @JsonSetter("empowermentproductid")
    public void setempowermentproductid(String empowermentproductid) {
        this.empowermentProductId = empowermentproductid;
    }

    public void setEmpowermentProductId(String empowermentProductId) {
        this.empowermentProductId = empowermentProductId;
    }

    @JsonSetter("tin")
    public void setTinLowerCase(String tin) {
        this.tin = tin;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }

    @JsonSetter("hasvat")
    public void sethasvat(Boolean hasvat) {
        this.hasVat = hasvat;
    }

    public void setHasVat(Boolean hasVat) {
        this.hasVat = hasVat;
    }

    @JsonSetter("hasexcise")
    public void sethasexcise(Boolean hasexcise) {
        this.hasExcise = hasexcise;
    }

    public void setHasExcise(Boolean hasExcise) {
        this.hasExcise = hasExcise;
    }

    @JsonSetter("hascomittent")
    public void sethascomittent(Boolean hascomittent) {
        this.hasComittent = hascomittent;
    }

    public void setHasComittent(Boolean hasComittent) {
        this.hasComittent = hasComittent;
    }

    @JsonSetter("hasmedical")
    public void sethasmedical(Boolean hasmedical) {
        this.hasMedical = hasmedical;
    }

    public void setHasMedical(Boolean hasMedical) {
        this.hasMedical = hasMedical;
    }

    @JsonSetter("haslgota")
    public void sethaslgota(Boolean haslgota) {
        this.hasLgota = haslgota;
    }

    public void setHasLgota(Boolean hasLgota) {
        this.hasLgota = hasLgota;
    }

    @JsonSetter("deliverysumwithvat")
    public void setdeliverysumwithvat(BigDecimal deliverysumwithvat) {
        this.deliverySumWithVat = deliverysumwithvat;
    }

    public void setDeliverySumWithVat(BigDecimal deliverySumWithVat) {
        this.deliverySumWithVat = deliverySumWithVat;
    }

    @JsonSetter("products")
    public void setProductsLowerCase(List<FacturaProductDTO> products) {
        this.products = products;
    }

    public void setProducts(List<FacturaProductDTO> products) {
        this.products = products;
    }
}
