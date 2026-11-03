package uz.uzkassa.smartpos.dto.fashion.balance;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FashionBalanceHistoryStatisticsDto {
    private Long productCount = 0L;
    private BigDecimal balanceStart = BigDecimal.ZERO;
    private BigDecimal balanceEnd = BigDecimal.ZERO;
}
