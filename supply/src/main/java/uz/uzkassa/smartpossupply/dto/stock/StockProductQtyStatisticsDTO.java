package uz.uzkassa.smartpossupply.dto.stock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 12.12.2023
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockProductQtyStatisticsDTO implements Serializable {

    BigDecimal stockInTotal;

    BigDecimal stockOutTotal;

    BigDecimal stockReturnTotal;

    BigDecimal stockAdjustmentTotal;

    BigDecimal stockTransferFromTotal;

    BigDecimal stockTransferToTotal;
}
