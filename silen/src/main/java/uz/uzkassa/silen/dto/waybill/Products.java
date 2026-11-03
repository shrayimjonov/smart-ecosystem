package uz.uzkassa.silen.dto.waybill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Products implements Serializable {
    private String id;
    private String productId;
    @JsonProperty("OrdNo")
    private Integer ordNo;
    @JsonProperty("CommittentTinOrPinfl")
    private String committentTinOrPinfl;
    @JsonProperty("CommittentName")
    private String committentName;
    @JsonProperty("ProductName")
    private String productName;
    @JsonProperty("CatalogCode")
    private String catalogCode;
    @JsonProperty("CatalogName")
    private String catalogName;
    @JsonProperty("PackageCode")
    private String packageCode;
    @JsonProperty("PackageName")
    private String packageName;
    @JsonProperty("Amount")
    private BigDecimal amount;
    @JsonProperty("Price")
    private BigDecimal price;
    @JsonProperty("DeliverySum")
    private BigDecimal deliverySum;
    @JsonProperty("WeightBrutto")
    private BigDecimal weightBrutto;
    @JsonProperty("WeightNetto")
    private BigDecimal weightNetto;
    private BigDecimal productWeightNetto;
    private BigDecimal productWeightBrutto;
    @JsonSetter("ordno")
    public void setordnoLowerCase(Integer ordno) {
        this.ordNo = ordno;
    }

    @JsonSetter("committenttinorpinfl")
    public void setcommittenttinorpinflLowerCase(String committenttinorpinfl) {
        this.committentTinOrPinfl = committenttinorpinfl;
    }

    @JsonSetter("committentname")
    public void setcommittentnameLowerCase(String committentname) {
        this.committentName = committentname;
    }

    @JsonSetter("productname")
    public void setproductnameLowerCase(String productname) {
        this.productName = productname;
    }

    @JsonSetter("catalogcode")
    public void setcatalogcodeLowerCase(String catalogcode) {
        this.catalogCode = catalogcode;
    }

    @JsonSetter("catalogname")
    public void setcatalognameLowerCase(String catalogname) {
        this.catalogName = catalogname;
    }

    @JsonSetter("packagecode")
    public void setpackagecodeLowerCase(String packagecode) {
        this.packageCode = packagecode;
    }

    @JsonSetter("packagename")
    public void setpackagenameLowerCase(String packagename) {
        this.packageName = packagename;
    }

    @JsonSetter("amount")
    public void setamountLowerCase(BigDecimal amount) {
        this.amount = amount;
    }

    @JsonSetter("price")
    public void setpriceLowerCase(BigDecimal price) {
        this.price = price;
    }

    @JsonSetter("deliverysum")
    public void setdeliverysumLowerCase(BigDecimal deliverysum) {
        this.deliverySum = deliverysum;
    }

    @JsonSetter("weightbrutto")
    public void setweightbruttoLowerCase(BigDecimal weightbrutto) {
        this.weightBrutto = weightbrutto;
    }

    @JsonSetter("weightnetto")
    public void setweightnettoLowerCase(BigDecimal weightnetto) {
        this.weightNetto = weightnetto;
    }
}
