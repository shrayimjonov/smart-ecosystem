package uz.uzkassa.silen.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DailyOperationsStats {
    private String productId;
    private BigDecimal production;
    private BigDecimal shipment;
    private BigDecimal returned;
    private BigDecimal dropout;

    public DailyOperationsStats(BigDecimal production, BigDecimal shipment, BigDecimal returned, BigDecimal dropout) {
        this.production = production;
        this.shipment = shipment;
        this.returned = returned;
        this.dropout = dropout;
    }

    public DailyOperationsStats() {
        this.production = BigDecimal.ZERO;
        this.shipment = BigDecimal.ZERO;
        this.returned = BigDecimal.ZERO;
        this.dropout = BigDecimal.ZERO;
    }
}
