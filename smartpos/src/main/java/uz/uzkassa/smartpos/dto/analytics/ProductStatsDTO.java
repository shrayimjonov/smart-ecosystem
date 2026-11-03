package uz.uzkassa.smartpos.dto.analytics;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ProductStatsDTO {
    Long id;

    String name;

    BigDecimal salesTotal;

    public ProductStatsDTO(Long id, String name, BigDecimal salesTotal) {
        this.id = id;
        this.name = name;
        this.salesTotal = salesTotal != null ? salesTotal : BigDecimal.ZERO;
    }
}
