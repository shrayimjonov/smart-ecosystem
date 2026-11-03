package uz.uzkassa.smartpossupply.dto.stockreport;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockBalanceReport extends CommonDTO {

    Long productId;

    String barcode;

    String vatBarcode;

    BigDecimal qty;

    CommonDTO branch;

    CommonDTO warehouse;
}
