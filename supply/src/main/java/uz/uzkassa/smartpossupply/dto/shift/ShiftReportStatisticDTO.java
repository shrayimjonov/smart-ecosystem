package uz.uzkassa.smartpossupply.dto.shift;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShiftReportStatisticDTO extends ShiftReportBaseTotalDTO {

    Long draftCount;

    Long shiftCount;

    @SuppressWarnings("unused")
    public ShiftReportStatisticDTO(BigDecimal totalCost, BigDecimal totalCash,
                                   BigDecimal totalCard, BigDecimal totalLoyaltyCard,
                                   BigDecimal totalDiscount, BigDecimal totalExcise,
                                   BigDecimal totalPaid, BigDecimal totalVat,
                                   Long receiptCount, Long draftCount,
                                   Long receiptReturnedCount, BigDecimal totalCashReturned,
                                   BigDecimal totalCardReturned, BigDecimal totalVatReturned,
                                   Long shiftCount) {
        this.totalCost = totalCost;
        this.totalCash = totalCash;
        this.totalCard = totalCard;
        this.totalLoyaltyCard = totalLoyaltyCard;
        this.totalDiscount = totalDiscount;
        this.totalExcise = totalExcise;
        this.totalPaid = totalPaid;
        this.totalVat = totalVat;
        this.receiptCount = receiptCount;
        this.draftCount = draftCount;
        this.receiptReturnedCount = receiptReturnedCount;
        this.totalCashReturned = totalCashReturned;
        this.totalCardReturned = totalCardReturned;
        this.totalVatReturned = totalVatReturned;
        this.shiftCount = shiftCount;
    }
}
