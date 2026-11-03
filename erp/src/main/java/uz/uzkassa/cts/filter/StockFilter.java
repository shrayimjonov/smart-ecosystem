package uz.uzkassa.cts.filter;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.StockItemType;
import uz.uzkassa.cts.enums.StockTransferType;

import java.util.List;

@Getter
@Setter
public class StockFilter extends BaseFilter {

    Long warehouseId;

    StockTransferType transferType;

    List<Long> variationOptionIds;

    Long unitId;

    Long itemId;

    StockItemType itemType;
}
