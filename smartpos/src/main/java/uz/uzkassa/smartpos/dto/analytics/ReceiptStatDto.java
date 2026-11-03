package uz.uzkassa.smartpos.dto.analytics;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ReceiptStatDto {
    private Long shifts;
    private Long count;
    private Long returnedCount;
    private BigDecimal total;
    private BigDecimal returnedTotal;
    private BigDecimal cash;
    private BigDecimal returnedCash;
    private BigDecimal discount;
    private BigDecimal returnedDiscount;
    private BigDecimal card;
    private BigDecimal returnedCard;
    private BigDecimal uzcard;
    private BigDecimal returnedUzcard;
    private BigDecimal humo;
    private BigDecimal returnedHumo;
    private BigDecimal loyalty;
    private BigDecimal returnedLoyalty;
    private BigDecimal other;
    private BigDecimal returnedOther;
    private BigDecimal vat;
    private BigDecimal returnedVat;
    private BigDecimal positions;
    private BigDecimal returnedPositions;
}
