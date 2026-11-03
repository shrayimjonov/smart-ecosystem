package uz.uzkassa.smartpossupply.dto.shift;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 17.01.2023 17:52
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ZReportStatisticsDTO {

    BigDecimal totalCard;

    BigDecimal totalCash;

    BigDecimal totalCost;

    @SuppressWarnings("unused")
    public ZReportStatisticsDTO(BigDecimal totalCard, BigDecimal totalCash) {
        this.totalCard = totalCard;
        this.totalCash = totalCash;
        this.totalCost = Optional.ofNullable(totalCash).orElse(BigDecimal.ZERO).add(Optional.ofNullable(totalCard).orElse(BigDecimal.ZERO));
    }
}
