package uz.uzkassa.smartpossupply.dto.stockreport;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 05.01.2023 13:49
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockProductStatisticsDTO {

    BigDecimal qty;

    String dataType;

    @SuppressWarnings("unused")
    public StockProductStatisticsDTO(BigDecimal qty, String dataType) {
        this.dataType = dataType;
        this.qty = qty;
    }
}
