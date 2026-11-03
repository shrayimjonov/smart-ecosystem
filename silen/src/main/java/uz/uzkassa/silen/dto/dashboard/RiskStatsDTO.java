package uz.uzkassa.silen.dto.dashboard;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.silen.enumeration.ProductType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RiskStatsDTO implements Serializable {
    private ProductType productType;
    private String name;
    private BigDecimal production;
    private BigDecimal shipment;
    private BigDecimal onWay;
    private BigDecimal delivered;
    private BigDecimal received;
    private List<RiskStatsDTO> data;

    public RiskStatsDTO(ProductType productType) {
        this.productType = productType;
        this.name = productType.getNameRu();
        this.production = BigDecimal.ZERO;
        this.shipment = BigDecimal.ZERO;
        this.onWay = BigDecimal.ZERO;
        this.delivered = BigDecimal.ZERO;
        this.received = BigDecimal.ZERO;
    }

    public RiskStatsDTO(String name) {
        this.name = name;
        this.production = BigDecimal.ZERO;
        this.shipment = BigDecimal.ZERO;
        this.onWay = BigDecimal.ZERO;
        this.delivered = BigDecimal.ZERO;
        this.received = BigDecimal.ZERO;
    }

    public List<RiskStatsDTO> getData() {
        if(data == null){
            data = new ArrayList<>();
        }
        return data;
    }
}
