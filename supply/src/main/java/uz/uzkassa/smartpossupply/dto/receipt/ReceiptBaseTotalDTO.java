package uz.uzkassa.smartpossupply.dto.receipt;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public class ReceiptBaseTotalDTO implements Serializable {
    BigDecimal totalCost;
    BigDecimal totalCash;
    BigDecimal totalCard;
    BigDecimal totalLoyaltyCard;
    BigDecimal totalDiscount;
    BigDecimal discountPercent;
    BigDecimal totalExcise;
    BigDecimal totalPaid;
    BigDecimal totalVat;

    public ReceiptBaseTotalDTO(BigDecimal totalCost, BigDecimal totalCash, BigDecimal totalCard, BigDecimal totalLoyaltyCard, BigDecimal totalDiscount, BigDecimal totalExcise, BigDecimal totalPaid, BigDecimal totalVat) {
        this.totalCost = totalCost;
        this.totalCash = totalCash;
        this.totalCard = totalCard;
        this.totalLoyaltyCard = totalLoyaltyCard;
        this.totalDiscount = totalDiscount;
        this.totalExcise = totalExcise;
        this.totalPaid = totalPaid;
        this.totalVat = totalVat;
    }
}
