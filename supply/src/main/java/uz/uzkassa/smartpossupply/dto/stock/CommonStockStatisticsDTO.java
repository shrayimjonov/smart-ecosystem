package uz.uzkassa.smartpossupply.dto.stock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 12.18.2022
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommonStockStatisticsDTO {

    Long checkingTotal;

    Long checkingErrorTotal;

    Long draftTotal;

    Long cancelledTotal;

    Long completedTotal;

    public CommonStockStatisticsDTO(
        Long checkingTotal,
        Long checkingErrorTotal,
        Long draftTotal,
        Long cancelledTotal,
        Long completedTotal
    ) {
        this.checkingTotal = checkingTotal;
        this.checkingErrorTotal = checkingErrorTotal;
        this.draftTotal = draftTotal;
        this.cancelledTotal = cancelledTotal;
        this.completedTotal = completedTotal;
    }
}
