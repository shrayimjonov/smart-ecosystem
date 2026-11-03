package uz.uzkassa.cts.dto.stockadjustment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockAdjustmentItemDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3603975937323L;

    Long id;

    Long itemId;

    List<Long> variationOptionIds;

    Long unitId;

    BigDecimal qty;

    BigDecimal vatRate;
}
