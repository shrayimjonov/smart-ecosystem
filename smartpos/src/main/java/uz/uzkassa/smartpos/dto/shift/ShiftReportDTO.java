package uz.uzkassa.smartpos.dto.shift;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.BusinessType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ShiftReportDTO implements Serializable {
    private Long number;
    private Long fiscalNumber;

    private Long branchId;
    private String branchName;
    private String branchAddress;

    private BusinessType businessType;
    private String companyName;
    private String companyINN;
    private LocalDateTime companyDateTime;
    private String terminalSN;
    private String fmNumber;

    private Long receipts;
    private Long receiptsReturned;

    private BigDecimal totalCash;
    private BigDecimal totalCard;
    private BigDecimal totalCashReturned;
    private BigDecimal totalCardReturned;

    private BigDecimal NDS;
    private BigDecimal ndsReturned;

    private Long userId;
    private String userName;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    private ShiftReportStatDTO statistics;
}

