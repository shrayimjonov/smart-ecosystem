package uz.uzkassa.smartpossupply.dto.shift;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.receipt.ReceiptBranchDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 17.01.2022 10:27
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationReportDTO {

    CompanyBaseDTO company;

    ReceiptBranchDTO branch;

    LocalDateTime registrationDate;

    String serialNumber;

    Long receiptCount;

    Long shiftCount;

    BigDecimal totalCash;

    BigDecimal totalCard;

    BigDecimal totalCost;

    BigDecimal totalVat;

    String fiscalNumber;

    @SuppressWarnings("unused")
    public RegistrationReportDTO(BigDecimal receiptCount, Long shiftCount, BigDecimal totalCash, BigDecimal totalCard, BigDecimal totalVat) {
        this.receiptCount = receiptCount.longValue();
        this.shiftCount = shiftCount;
        this.totalCash = Optional.ofNullable(totalCash).orElse(BigDecimal.ZERO);
        this.totalCard = Optional.ofNullable(totalCard).orElse(BigDecimal.ZERO);
        this.totalCost = this.totalCash.add(this.totalCard);
        this.totalVat = totalVat;
    }
}
