package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.stocktransfer.StockTransfer;
import uz.uzkassa.horeca.domain.stocktransfer.StockTransferItem;

import java.util.List;

public interface StockTransferItemRepository extends BaseRepository<StockTransferItem, Long> {
    List<StockTransferItem> findAllByStockTransferId(Long stockTransferId);
}
