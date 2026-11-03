package uz.uzkassa.smartpos.dto.terminal.sales;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.PaymentType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TransactionStatisticsDto {
    private List<PaymentTypeStatisticsDto> paymentTypeStatistics = new ArrayList<>();

    public Long getTotal() {
        return paymentTypeStatistics != null ? paymentTypeStatistics.stream().map(PaymentTypeStatisticsDto::getTotal).reduce(0L, Long::sum) : 0;
    }

    @SuppressWarnings("unused")
    public BigDecimal getTotalAmount() {
        return paymentTypeStatistics != null ? paymentTypeStatistics.stream().map(PaymentTypeStatisticsDto::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add) : null;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentTypeStatisticsDto {
        private PaymentType paymentType;
        private Long total;
        private BigDecimal totalAmount;
    }
}
