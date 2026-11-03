package uz.uzkassa.smartpossupply.dto.waybill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WaybillProductDTO implements Serializable {
    Long id;
    Long productId;
    CommonDTO branch;
    BigDecimal brutto;
    BigDecimal netto;

    @JsonProperty("OrdNo")
    Integer ordNo;
    @JsonProperty("CommittentTinOrPinfl")
    String committentTinOrPinfl;
    @JsonProperty("CommittentName")
    String committentName;
    @JsonProperty("ProductName")
    String productName;
    @JsonProperty("CatalogCode")
    String catalogCode;
    @JsonProperty("CatalogName")
    String catalogName;
    @JsonProperty("PackageCode")
    String packageCode;
    @JsonProperty("PackageName")
    String packageName;
    @JsonProperty("Amount")
    BigDecimal amount;
    @JsonProperty("Price")
    BigDecimal price;
    @JsonProperty("DeliverySum")
    BigDecimal deliverySum;
    @JsonProperty("WeightBrutto")
    BigDecimal weightBrutto;
    @JsonProperty("WeightNetto")
    BigDecimal weightNetto;

    @JsonSetter("ordno")
    public void setordnoToLowerCase(Integer ordno) {
        this.ordNo = ordno;
    }

    @JsonSetter("committenttinorpinfl")
    public void setcommittenttinorpinflToLowerCase(String committenttinorpinfl) {
        this.committentTinOrPinfl = committenttinorpinfl;
    }

    @JsonSetter("committentname")
    public void setcommittentnameToLowerCase(String committentname) {
        this.committentName = committentname;
    }

    @JsonSetter("productname")
    public void setproductnameToLowerCase(String productname) {
        this.productName = productname;
    }

    @JsonSetter("catalogcode")
    public void setcatalogcodeToLowerCase(String catalogcode) {
        this.catalogCode = catalogcode;
    }

    @JsonSetter("catalogname")
    public void setcatalognameToLowerCase(String catalogname) {
        this.catalogName = catalogname;
    }

    @JsonSetter("packagecode")
    public void setpackagecodeToLowerCase(String packagecode) {
        this.packageCode = packagecode;
    }

    @JsonSetter("packagename")
    public void setpackagenameToLowerCase(String packagename) {
        this.packageName = packagename;
    }

    @JsonSetter("amount")
    public void setamountToLowerCase(BigDecimal amount) {
        this.amount = amount;
    }

    @JsonSetter("price")
    public void setpriceToLowerCase(BigDecimal price) {
        this.price = price;
    }

    @JsonSetter("deliverysum")
    public void setdeliverysumToLowerCase(BigDecimal deliverysum) {
        this.deliverySum = deliverysum;
    }

    @JsonSetter("weightbrutto")
    public void setweightbruttoToLowerCase(BigDecimal weightbrutto) {
        this.weightBrutto = weightbrutto;
    }

    @JsonSetter("weightnetto")
    public void setweightnettoToLowerCase(BigDecimal weightnetto) {
        this.weightNetto = weightnetto;
    }
}
