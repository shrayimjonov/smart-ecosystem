package uz.uzkassa.smartpos.dto.shift;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftTotalStatisticsDTO {
    private String companyName;
    private String companyAddress;
    private LocalDateTime registrationDate;
    private String inn;
    private String branchName;
    private String branchAddress;
    private String fmNumber;
    private String serialNumber;
    private Long receipts;
    private Long receiptsReturned;
    private BigDecimal totalCash;
    private BigDecimal totalCashReturned;
    private BigDecimal totalCard;
    private BigDecimal totalCardReturned;
    private BigDecimal nds;
    private BigDecimal ndsReturned;

    private BigDecimal discount;
    private Long drafts;
    private Long shifts;

}
