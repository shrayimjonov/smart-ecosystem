package uz.uzkassa.smartpossupply.dto.stockreport;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 05.01.2023 13:49
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockStatisticsDTO {

    String dataType;

    Long total;

    @SuppressWarnings("unused")
    public StockStatisticsDTO(String dataType, Long total) {
        this.dataType = dataType;
        this.total = total;
    }
}
