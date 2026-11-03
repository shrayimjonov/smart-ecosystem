package uz.uzkassa.smartpos.dto.scheduling.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MonthlyScheduleResponse {
    private LocalDateTime dueDateTime;
    private LocalDateTime paidDateTime;
    private BigDecimal totalCost = BigDecimal.ZERO;
    private BigDecimal totalVat = BigDecimal.ZERO;
    private BigDecimal totalPaid = BigDecimal.ZERO;
    private BigDecimal totalPaidVat = BigDecimal.ZERO;
    private List<ProductScheduleResponse> products = new ArrayList<>();
}
