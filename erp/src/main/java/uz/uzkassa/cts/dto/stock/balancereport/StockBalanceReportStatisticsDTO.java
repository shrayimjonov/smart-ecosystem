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
public class StockBalanceReportStatisticsDTO implements Serializable {

    BigDecimal stockInTotal;

    BigDecimal stockOutTotal;

    BigDecimal stockReturnTotal;

    BigDecimal stockAdjustmentTotal;

    BigDecimal stockTransferFromTotal;

    BigDecimal stockTransferToTotal;
}
