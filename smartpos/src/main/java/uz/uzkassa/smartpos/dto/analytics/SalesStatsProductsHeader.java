package uz.uzkassa.smartpos.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalesStatsProductsHeader {
    private Long totalProductCount;

    private BigDecimal totalDiscount;

    private BigDecimal totalNds;

    private BigDecimal totalSum;
}
