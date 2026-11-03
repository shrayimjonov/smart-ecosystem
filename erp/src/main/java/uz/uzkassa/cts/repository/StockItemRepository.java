package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.stock.StockItem;
import uz.uzkassa.cts.repository.custom.StockItemRepositoryCustom;

@Repository
public interface StockItemRepository extends BaseRepository<StockItem, Long>, StockItemRepositoryCustom {
}
