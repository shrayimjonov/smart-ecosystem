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
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductInfoDTO implements Serializable {

    @JsonProperty("TotalDeliverySum")
    private BigDecimal totalDeliverySum;

    @JsonProperty("TotalWeightBrutto")
    private BigDecimal totalWeightBrutto;

    @JsonProperty("Products")
    private List<Products> products;

    @JsonSetter("totaldeliverysum")
    public void settotaldeliverysumLowerCase(BigDecimal totaldeliverysum) {
        this.totalDeliverySum = totaldeliverysum;
    }

    @JsonSetter("totalweightbrutto")
    public void settotalweightbruttoLowerCase(BigDecimal totalweightbrutto) {
        this.totalWeightBrutto = totalweightbrutto;
    }

    @JsonSetter("products")
    public void setProductsLowerCase(List<Products> products) {
        this.products = products;
    }
}
