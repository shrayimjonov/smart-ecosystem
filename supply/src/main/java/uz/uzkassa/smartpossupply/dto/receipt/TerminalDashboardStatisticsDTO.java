package uz.uzkassa.smartpossupply.dto.receipt;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 09.11.2024
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerminalDashboardStatisticsDTO {

    Long receiptCount;

    Long shiftCount;

    BigDecimal totalSales;

    BigDecimal totalCash;

    BigDecimal totalCard;

    Long stockOutCount;

    Long orderCount;

    BigDecimal productCount;

    public TerminalDashboardStatisticsDTO(Long receiptCount, Long shiftCount, BigDecimal totalSales, BigDecimal totalCash, BigDecimal totalCard, Long stockOutCount, Long orderCount, BigDecimal productCount) {
        this.receiptCount = receiptCount;
        this.shiftCount = shiftCount;
        this.totalSales = totalSales;
        this.totalCash = totalCash;
        this.totalCard = totalCard;
        this.stockOutCount = stockOutCount;
        this.orderCount = orderCount;
        this.productCount = productCount;
    }
}
