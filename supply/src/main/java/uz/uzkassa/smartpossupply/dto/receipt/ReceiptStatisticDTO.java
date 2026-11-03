package uz.uzkassa.smartpossupply.dto.receipt;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 20.11.2024
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReceiptStatisticDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3437611L;

    Long shiftCount;

    Long receipts;

    Long returnedReceipts;

    BigDecimal revenue;

    BigDecimal totalReturnedRevenue;

    BigDecimal cash;

    BigDecimal returnedCash;

    BigDecimal card;

    BigDecimal returnedCard;

    BigDecimal cardUzCard;

    BigDecimal returnedUZCARD;

    BigDecimal cardHumo;

    BigDecimal returnedHUMO;

    BigDecimal cardLoyalty;

    BigDecimal returnedLoyalty;

    BigDecimal cardOther;

    BigDecimal returnedOther;

    BigDecimal nds;

    BigDecimal returnedNDS;

    Long positionsInReceipts;

    Long positionsInReturnedReceipts;

    BigDecimal discount;

    BigDecimal returnedDiscount;
}