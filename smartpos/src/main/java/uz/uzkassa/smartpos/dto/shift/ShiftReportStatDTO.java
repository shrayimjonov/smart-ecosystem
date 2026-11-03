package uz.uzkassa.smartpos.dto.shift;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ShiftReportStatDTO {
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

    public ShiftReportStatDTO(Long receipts, Long receiptsReturned, BigDecimal totalCash, BigDecimal totalCashReturned, BigDecimal totalCard, BigDecimal totalCardReturned, BigDecimal NDS, BigDecimal NDSReturned,Long shifts) {
        this.receipts = receipts;
        this.receiptsReturned = receiptsReturned;
        this.totalCash = totalCash;
        this.totalCashReturned = totalCashReturned;
        this.totalCard = totalCard;
        this.totalCardReturned = totalCardReturned;
        this.nds = NDS;
        this.ndsReturned = NDSReturned;
        this.shifts = shifts;
    }
}
