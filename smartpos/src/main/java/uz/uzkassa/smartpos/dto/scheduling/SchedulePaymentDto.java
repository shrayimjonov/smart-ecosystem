package uz.uzkassa.smartpos.dto.scheduling;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SchedulePaymentDto(BigDecimal paid, BigDecimal vat, LocalDateTime paidDateTime) {
}
