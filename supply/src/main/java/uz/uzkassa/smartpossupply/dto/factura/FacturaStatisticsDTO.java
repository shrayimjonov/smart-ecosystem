package uz.uzkassa.smartpossupply.dto.factura;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class FacturaStatisticsDTO {

    BigDecimal amount;

    Long byDay;

    Long byWeek;

    Long byMonth;

    Long byYear;

    @SuppressWarnings("unused")
    public FacturaStatisticsDTO(BigDecimal amount, Long byDay, Long byWeek, Long byMonth, Long byYear) {
        this.amount = amount;
        this.byDay = byDay;
        this.byWeek = byWeek;
        this.byMonth = byMonth;
        this.byYear = byYear;
    }

    @SuppressWarnings("unused")
    public FacturaStatisticsDTO(BigDecimal amount, Long byWeek, Long byMonth, Long byYear) {
        this.amount = amount;
        this.byWeek = byWeek;
        this.byMonth = byMonth;
        this.byYear = byYear;
    }

    @SuppressWarnings("unused")
    public FacturaStatisticsDTO(BigDecimal amount, Long byMonth, Long byYear) {
        this.amount = amount;
        this.byMonth = byMonth;
        this.byYear = byYear;
    }
}
