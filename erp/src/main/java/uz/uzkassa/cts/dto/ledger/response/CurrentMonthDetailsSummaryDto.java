package uz.uzkassa.cts.dto.ledger.response;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CurrentMonthDetailsSummaryDto implements Serializable {
    private String tariffName;
    private BigDecimal amount;
    private Long count;

    public CurrentMonthDetailsSummaryDto(String tariffName, BigDecimal amount, Long count) {
        this.tariffName = tariffName;
        this.amount = amount;
        this.count = count;
    }

    public CurrentMonthDetailsSummaryDto() {
    }
}
