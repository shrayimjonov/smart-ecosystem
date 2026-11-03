package uz.uzkassa.silen.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AggregationSum {
    private String organizationId;
    private String serialId;
    private String productId;
    private BigDecimal quantity;

    public AggregationSum() {
    }

    public AggregationSum(String organizationId, String productId, String serialId, BigDecimal quantity) {
        this.organizationId = organizationId;
        this.serialId = serialId;
        this.productId = productId;
        this.quantity = quantity;
    }
}
