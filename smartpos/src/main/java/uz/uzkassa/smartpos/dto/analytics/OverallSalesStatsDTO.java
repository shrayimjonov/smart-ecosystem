package uz.uzkassa.smartpos.dto.analytics;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class OverallSalesStatsDTO {
    private BigDecimal salesTotal;
    private Long salesCount;
    private BigDecimal averageReceiptCost;
    private BigDecimal salesCash;
    private BigDecimal salesCard;
    private BigDecimal discount;
    private BigDecimal nds;
    private BigDecimal excise;
    private BigDecimal returned;
    private LocalDateTime dateTime;
}
