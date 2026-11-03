package uz.uzkassa.silen.dto.dashboard;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.silen.enumeration.PackageType;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class DashboardStatsDTO implements Serializable {
    private PackageType unit;
    protected String productId;
    protected String productName;
    protected BigDecimal quantity;
    protected String organizationId;

    public DashboardStatsDTO(String organizationId, String productId, Integer unit, BigDecimal quantity) {
        this.productId = productId;
        this.quantity = quantity;
        this.organizationId = organizationId;
        this.unit = unit != null ? PackageType.fromCode(unit) : PackageType.BOTTLE;
    }

    public DashboardStatsDTO(String productId, String unit, BigDecimal quantity) {
        this.productId = productId;
        this.quantity = quantity;
        this.unit = unit != null ? PackageType.valueOf(unit) : PackageType.BOTTLE;
    }
}
