package uz.uzkassa.horeca.dto.stockadjustment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockAdjustmentItemDTO implements Serializable {

    Long id;

    Long productId;

    BigDecimal qty;

    BigDecimal price;

    Long warehouseId;

    List<String> aggregationCodes;

    List<String> markCodes;

    List<String> blockCodes;
}
