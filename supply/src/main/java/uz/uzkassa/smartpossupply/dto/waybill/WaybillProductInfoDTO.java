package uz.uzkassa.smartpossupply.dto.waybill;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WaybillProductInfoDTO implements Serializable {

    @JsonProperty("TotalDeliverySum")
    private BigDecimal totalDeliverySum;

    @JsonProperty("TotalWeightBrutto")
    private BigDecimal totalWeightBrutto;

    @JsonProperty("Products")
    private List<WaybillProductDTO> products;

    @JsonSetter("totaldeliverysum")
    public void settotaldeliverysumToLowerCase(BigDecimal totaldeliverysum) {
        this.totalDeliverySum = totaldeliverysum;
    }

    @JsonSetter("totalweightbrutto")
    public void settotalweightbruttoToLowerCase(BigDecimal totalweightbrutto) {
        this.totalWeightBrutto = totalweightbrutto;
    }

    @JsonSetter("products")
    public void setProductsToLowerCase(List<WaybillProductDTO> products) {
        this.products = products;
    }

    @JsonIgnore
    public void addProduct(WaybillProductDTO productDTO) {
        if (products == null) {
            products = new ArrayList<>();
        }
        products.add(productDTO);
    }
}
