package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.stock.StockProduct;

@Repository
public interface StockInProductRepository extends BaseRepository<StockProduct, Long>, StockInProductRepositoryCustom {
}
