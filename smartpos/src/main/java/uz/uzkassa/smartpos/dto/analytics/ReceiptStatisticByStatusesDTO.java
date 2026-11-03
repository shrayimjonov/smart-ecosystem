package uz.uzkassa.smartpos.dto.analytics;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ReceiptStatisticByStatusesDTO {
    private BigDecimal paidCash;
    private BigDecimal paidCard;
    private BigDecimal returnedCash;
    private BigDecimal returnedCard;
    private BigDecimal income;
    private BigDecimal returnFlow;
    private BigDecimal withdraw;
    private BigDecimal flow;
    private BigDecimal incassation;
}
