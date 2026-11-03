package uz.uzkassa.cts.filter;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.StockItemType;

@Getter
@Setter
public class ProductFilter extends BaseFilter {

    Long brandId;

    StockItemType itemType;
}
