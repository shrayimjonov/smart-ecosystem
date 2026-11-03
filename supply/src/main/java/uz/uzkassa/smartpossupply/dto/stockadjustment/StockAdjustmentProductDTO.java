package uz.uzkassa.smartpossupply.dto.stockadjustment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockAdjustmentProductDTO implements Serializable {

    Long id;

    Long productSerialId;

    String barcode;

    String vatBarcode;

    BigDecimal qty;

    BigDecimal price;

    BigDecimal foreignPrice;

    BigDecimal qtyInWarehouse;

    Long stockAdjustmentProductId;

    String otherReason;

    List<String> aggregationCodes = new ArrayList<>();

    List<String> markCodes = new ArrayList<>();

    List<String> aggregationBlockCodes = new ArrayList<>();
}
