package uz.uzkassa.cts.dto.stocktransfer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.StockItemType;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockTransferItemDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1049024L;

    Long id;

    Long stockTransferItemId;

    Long itemId;

    StockItemType itemType;

    BigDecimal qty;

    Long unitId;

    List<Long> variationOptionIds;
}
