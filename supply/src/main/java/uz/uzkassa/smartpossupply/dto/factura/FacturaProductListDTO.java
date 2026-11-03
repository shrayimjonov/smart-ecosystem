package uz.uzkassa.smartpossupply.dto.factura;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
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

    @JsonProperty("HasCommittent")
    private Boolean hasCommittent = Boolean.FALSE;

    @JsonProperty("HasLgota")
    private Boolean hasLgota = Boolean.FALSE;

    @JsonProperty("HasMedical")
    private Boolean hasMedical;

    @JsonProperty("HideReportCommittent")
    private Boolean hideReportCommittent = Boolean.FALSE;

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
        this.hasVat = Optional.ofNullable(hasvat).orElse(Boolean.FALSE);
    }

    public void setHasVat(Boolean hasVat) {
        this.hasVat = Optional.ofNullable(hasVat).orElse(Boolean.FALSE);
    }

    @JsonSetter("hasexcise")
    public void sethasexcise(Boolean hasexcise) {
        this.hasExcise = Optional.ofNullable(hasexcise).orElse(Boolean.FALSE);
    }

    public void setHasExcise(Boolean hasExcise) {
        this.hasExcise = Optional.ofNullable(hasExcise).orElse(Boolean.FALSE);
    }

    @JsonSetter("hascommittent")
    public void sethascommittent(Boolean hasCommittent) {
        this.hasCommittent = Optional.ofNullable(hasCommittent).orElse(Boolean.FALSE);
    }

    public void setHasCommittent(Boolean hasCommittent) {
        this.hasCommittent = Optional.ofNullable(hasCommittent).orElse(Boolean.FALSE);
    }

    @JsonSetter("hasmedical")
    public void sethasmedical(Boolean hasmedical) {
        this.hasMedical = Optional.ofNullable(hasmedical).orElse(Boolean.FALSE);
    }

    public void setHasMedical(Boolean hasMedical) {
        this.hasMedical = Optional.ofNullable(hasMedical).orElse(Boolean.FALSE);
    }

    @JsonSetter("haslgota")
    public void sethaslgota(Boolean haslgota) {
        this.hasLgota = Optional.ofNullable(haslgota).orElse(Boolean.FALSE);
    }

    public void setHasLgota(Boolean hasLgota) {
        this.hasLgota = Optional.ofNullable(hasLgota).orElse(Boolean.FALSE);
    }

    @JsonSetter("hidereportcommittent")
    public void sethidereportcommittent(Boolean hidereportcommittent) {
        this.hideReportCommittent = Optional.ofNullable(hidereportcommittent).orElse(Boolean.FALSE);
    }

    public void setHideReportCommittent(Boolean hideReportCommittent) {
        this.hideReportCommittent = Optional.ofNullable(hideReportCommittent).orElse(Boolean.FALSE);
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
