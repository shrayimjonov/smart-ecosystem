package uz.uzkassa.smartpos.dto.analytics;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CategorySalesStats {
    private BigDecimal salesTotal = BigDecimal.ZERO;
    private BigDecimal salesCount = BigDecimal.ZERO;
    private BigDecimal discount = BigDecimal.ZERO;
    private Long receiptCount = 0L;
    private BigDecimal nds = BigDecimal.ZERO;

    public CategorySalesStats(SalesStatsDTO categorySalesStatsDTO) {
        this.salesTotal = categorySalesStatsDTO.getSalesTotal();
        this.salesCount = categorySalesStatsDTO.getSalesCount();
        this.discount = categorySalesStatsDTO.getDiscount();
        this.nds = categorySalesStatsDTO.getNds();
        this.receiptCount = categorySalesStatsDTO.getReceiptCount();
    }

    public void combine(CategorySalesStats categorySalesStats) {
        if (categorySalesStats == null)
            return;
        salesTotal = salesTotal.add(categorySalesStats.getSalesTotal());
        salesCount = salesCount.add(categorySalesStats.getSalesCount());
        if ((categorySalesStats.getDiscount() != null) && (discount != null))
            discount = discount.add(categorySalesStats.getDiscount());
        if ((categorySalesStats.getNds() != null) && (nds != null))
            nds = nds.add(categorySalesStats.getNds());
        receiptCount = receiptCount + categorySalesStats.getReceiptCount();
    }
}
