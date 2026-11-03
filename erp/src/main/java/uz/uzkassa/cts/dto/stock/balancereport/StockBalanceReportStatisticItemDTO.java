package uz.uzkassa.cts.dto.stock.balancereport;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockBalanceReportStatisticItemDTO implements Serializable {

    BigDecimal qty;

    String dataType;

    @SuppressWarnings("unused")
    public StockBalanceReportStatisticItemDTO(BigDecimal qty, String dataType) {
        this.dataType = dataType;
        this.qty = qty;
    }
}
