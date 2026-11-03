package uz.uzkassa.smartpossupply.dto.stocktransfer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 19.12.2022
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockTransferStatisticsDTO {

    Long draftTotal;

    Long inTransitTotal;

    Long rejectedTotal;

    Long approvedTotal;

    public StockTransferStatisticsDTO(Long draftTotal, Long inTransitTotal, Long rejectedTotal, Long approvedTotal) {
        this.draftTotal = draftTotal;
        this.inTransitTotal = inTransitTotal;
        this.rejectedTotal = rejectedTotal;
        this.approvedTotal = approvedTotal;
    }
}
