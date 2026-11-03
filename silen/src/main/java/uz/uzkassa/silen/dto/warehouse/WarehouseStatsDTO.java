package uz.uzkassa.silen.dto.warehouse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class WarehouseStatsDTO {
    private String organizationId;
    private String organizationName;
    private String organizationType;

    private String toOrganizationId;
    private String toOrganizationName;
    private String toOrganizationType;
    private String productType;
    private BigDecimal production;
    private BigDecimal shipment;
    private BigDecimal losses;
    private BigDecimal balance;
    private BigDecimal income;

    public WarehouseStatsDTO(String organizationId, String organizationName, String organizationType, String productType, BigDecimal production, BigDecimal losses,BigDecimal shipment, BigDecimal income, BigDecimal balance) {
        this.organizationId = organizationId;
        this.organizationName = organizationName;
        this.organizationType = organizationType;
        this.productType = productType;
        this.production = production;
        this.losses = losses;
        this.shipment = shipment;
        this.income = income;
        this.balance = balance;
    }

    public WarehouseStatsDTO(String productType, BigDecimal production, BigDecimal shipment, BigDecimal losses, BigDecimal balance, BigDecimal income) {
        this.productType = productType;
        this.production = production;
        this.shipment = shipment;
        this.losses = losses;
        this.balance = balance;
        this.income = income;

    }
}
