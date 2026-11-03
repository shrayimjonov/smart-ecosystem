package uz.uzkassa.smartpos.dto.customerreceipts;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
@ToString
@Slf4j
public class BranchReceiptsByMonthDTO {

    private Long branchId;
    private String branchName;
    private BigDecimal revenue;
    private BigDecimal NDS;
    private BigDecimal excise;
    private Long receipts;
    private BigDecimal receiptsAvg;
    private BigDecimal cardUzCard;
    private BigDecimal cardHumo;
    private BigDecimal cash;
    private Long terminals;

    private Float revenuePercent;
    private Float NDSPercent;
    private Float excisePercent;
    private Float receiptsPercent;
    private Float receiptsAvgPercent;
    private Float cardUzCardPercent;
    private Float cardHumoPercent;
    private Float cashPercent;

    public BranchReceiptsByMonthDTO(String branchName, Long branchId, BigDecimal revenue, BigDecimal NDS,
                                    BigDecimal excise, Long receipts, BigDecimal receiptsAvg,
                                    BigDecimal cardUzCard, BigDecimal cardHumo,
                                    BigDecimal cash, Long terminals) {
        this.branchName = branchName;
        this.branchId = branchId;
        this.revenue = revenue;
        this.NDS = NDS;
        this.excise = excise;
        this.receipts = receipts;
        this.receiptsAvg = receiptsAvg;
        this.cardUzCard = cardUzCard;
        this.cardHumo = cardHumo;
        this.cash = cash;
        this.terminals = terminals;
    }

    public void setPercent(BranchReceiptsByMonthDTO dto) {

        this.revenuePercent = calcPercent(this.revenue, dto.revenue);
        this.NDSPercent = calcPercent(this.NDS, dto.NDS);
        this.excisePercent = calcPercent(this.excise, dto.excise);
        this.receiptsPercent = calcPercent(this.receipts, dto.receipts);
        this.receiptsAvgPercent = calcPercent(this.receiptsAvg, dto.receiptsAvg);
        this.cardUzCardPercent = calcPercent(this.cardUzCard, dto.cardUzCard);
        this.cardHumoPercent = calcPercent(this.cardHumo, dto.cardHumo);
        this.cashPercent = calcPercent(this.cash, dto.cash);
    }

    private Float calcPercent(Long thisValue, Long dtoValue) {
        try {
            if (thisValue > dtoValue) {
                return (float) ((thisValue / dtoValue) - 1) * 100;
            } else
                return (float) ((dtoValue / thisValue) - 1) * -100;
        } catch (ArithmeticException e) {
            log.error(e.getMessage());
            return 0F;
        }
    }

    private Float calcPercent(BigDecimal thisValue, BigDecimal dtoValue) {
        if (BigDecimal.ZERO.compareTo(thisValue) == 0 || BigDecimal.ZERO.compareTo(dtoValue) == 0) {
            return 0F;
        }
        try {
            if (thisValue.compareTo(dtoValue) > 0) {
                return (thisValue.divide(dtoValue, RoundingMode.HALF_UP)).subtract(BigDecimal.valueOf(1)).floatValue() * 100;
            } else
                return dtoValue.divide(thisValue, RoundingMode.HALF_UP).subtract(BigDecimal.valueOf(1)).floatValue() * -100;
        } catch (ArithmeticException e) {
            log.error(e.getMessage());
            return 0F;
        }
    }


}
