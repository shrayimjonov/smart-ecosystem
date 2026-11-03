package uz.uzkassa.silen.dto.dashboard;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.silen.enumeration.ProductType;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class StatsV2DTO implements Serializable {
    private ProductType productType;
    private String type;
    private String productName;
    private BigDecimal production;
    private BigDecimal shipment;
    private BigDecimal losses;
    private BigDecimal balance;
    private BigDecimal income;

    public StatsV2DTO(String type, BigDecimal production, BigDecimal shipment/*, BigDecimal losses*/) {
        this.productType = StringUtils.isNotEmpty(type) ? ProductType.valueOf(type) : null;
        this.type = this.productType != null ? this.productType.getNameRu() : null;
        this.production = production;
        this.shipment = shipment;
        this.losses = BigDecimal.ZERO;
        this.balance = production.subtract(shipment);
    }
    public StatsV2DTO(String type, BigDecimal production, BigDecimal shipment, BigDecimal losses,BigDecimal balance, BigDecimal income) {
        this.productType = StringUtils.isNotEmpty(type) ? ProductType.valueOf(type) : null;
        this.type = this.productType != null ? this.productType.getNameRu() : null;
        this.production = production;
        this.shipment = shipment;
        this.losses = losses;
        this.balance = balance;
        this.income = income;
    }

    public StatsV2DTO(String type, String productName, BigDecimal production, BigDecimal shipment/*, BigDecimal losses*/) {
        this.productType = StringUtils.isNotEmpty(type) ? ProductType.valueOf(type) : null;
        this.type = this.productType != null ? this.productType.getNameRu() : null;
        this.productName = productName;
        this.production = production;
        this.shipment = shipment;
        this.losses = BigDecimal.ZERO;
        this.balance = production.subtract(shipment);
    }

    public StatsV2DTO(ProductType productType) {
        this.productType = productType;
        this.type = this.productType != null ? this.productType.getNameRu() : null;
        this.production = BigDecimal.ZERO;
        this.shipment = BigDecimal.ZERO;
        this.losses = BigDecimal.ZERO;
        this.balance = BigDecimal.ZERO;
    }

    public StatsV2DTO(String productName) {
        this.productName = productName;
        this.production = BigDecimal.ZERO;
        this.shipment = BigDecimal.ZERO;
        this.losses = BigDecimal.ZERO;
        this.balance = BigDecimal.ZERO;
    }

    public void updateStats(StatsV2DTO sumDto) {
        setProduction(getProduction());
        setShipment(getShipment());
        setLosses(getLosses());
        setBalance(getBalance());
    }

}
