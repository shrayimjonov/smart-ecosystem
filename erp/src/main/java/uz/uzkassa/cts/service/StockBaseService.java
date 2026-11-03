package uz.uzkassa.cts.service;

import uz.uzkassa.cts.dto.stock.StockItemDTO;
import uz.uzkassa.cts.enums.StockItemType;

public interface StockBaseService<T, ID> extends BaseService<T, ID> {

    StockItemDTO getItem(Long itemId, StockItemType stockItemType);
}
