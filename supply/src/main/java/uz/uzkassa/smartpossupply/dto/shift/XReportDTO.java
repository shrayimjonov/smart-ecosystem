package uz.uzkassa.smartpossupply.dto.shift;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 27.12.2022 16:23
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class XReportDTO implements Serializable {

    Long receiptCount;

    Long returnedReceiptCount;

    BigDecimal totalCard;

    BigDecimal totalReturnedCard;

    BigDecimal totalVat;

    BigDecimal totalReturnedVat;

    BigDecimal totalCash;

    BigDecimal totalReturnedCash;

    @SuppressWarnings("unused")
    public XReportDTO(Long receiptCount, Long returnedReceiptCount,
                      BigDecimal totalCard, BigDecimal totalReturnedCard,
                      BigDecimal totalVat, BigDecimal totalReturnedVat,
                      BigDecimal totalCash, BigDecimal totalReturnedCash) {
        this.receiptCount = receiptCount;
        this.returnedReceiptCount = returnedReceiptCount;
        this.totalCard = Optional.ofNullable(totalCard).orElse(BigDecimal.ZERO);
        this.totalReturnedCard = totalReturnedCard;
        this.totalVat = totalVat;
        this.totalReturnedVat = totalReturnedVat;
        this.totalCash = Optional.ofNullable(totalCash).orElse(BigDecimal.ZERO);
        this.totalReturnedCash = totalReturnedCash;
    }
}
