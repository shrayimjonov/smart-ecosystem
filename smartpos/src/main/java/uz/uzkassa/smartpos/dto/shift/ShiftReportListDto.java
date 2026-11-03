package uz.uzkassa.smartpos.dto.shift;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.receipt.ShiftReport}
 */
@Getter
@Setter
public class ShiftReportListDto implements Serializable {
    private Long id;
    private Long fiscalNumber;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Long receipts;
    private Long receiptsReturned;
    private BigDecimal card;
    private BigDecimal cardReturned;
    private BigDecimal cash;
    private BigDecimal cashReturned;
    private BigDecimal NDS;
    private BigDecimal ndsReturned;
    private String terminalSN;
    private Long branchId;
    private String branchName;
}