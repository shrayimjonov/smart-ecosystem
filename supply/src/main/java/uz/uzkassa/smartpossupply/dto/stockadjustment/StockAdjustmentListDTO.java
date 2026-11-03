package uz.uzkassa.smartpossupply.dto.stockadjustment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.CurrencyType;
import uz.uzkassa.smartpossupply.enums.StockAdjustmentStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockAdjustmentListDTO implements Serializable {

    Long id;

    String adjustmentNumber;

    LocalDateTime adjustmentDate;

    CommonDTO branch;

    CommonDTO warehouse;

    StockAdjustmentStatus status;

    BigDecimal totalQty;

    BigDecimal totalPrice;

    BigDecimal totalForeignPrice;

    CurrencyType currencyType;

    CommonDTO creator;

    LocalDateTime lastModifiedDate;
}
