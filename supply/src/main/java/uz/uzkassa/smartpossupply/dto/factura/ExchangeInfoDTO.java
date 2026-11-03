package uz.uzkassa.smartpossupply.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class ExchangeInfoDTO implements Serializable {
    @JsonProperty("ProductCode")
    private String productCode;
    @JsonProperty("ProductProperties")
    private String productProperties;
    @JsonProperty("PlanPositionId")
    private Integer planPositionId;

    @JsonSetter("productcode")
    public void setproductcode(String productcode) {
        this.productCode = productcode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    @JsonSetter("productproperties")
    public void setproductproperties(String productproperties) {
        this.productProperties = productproperties;
    }

    public void setProductProperties(String productProperties) {
        this.productProperties = productProperties;
    }

    @JsonSetter("planpositionid")
    public void setplanpositionid(Integer planpositionid) {
        this.planPositionId = planpositionid;
    }

    public void setPlanPositionId(Integer planPositionId) {
        this.planPositionId = planPositionId;
    }
}
