package uz.uzkassa.smartpossupply.dto.stockout;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 11.12.2023
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockOutLookUpDTO extends CommonDTO {

    CommonDTO factura;

    BigDecimal limitBalance;

    BigDecimal currentBalance;

    BigDecimal totalPrice;
}
