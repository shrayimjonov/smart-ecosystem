package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.WarehouseHistory;

@Repository
public interface WarehouseHistoryRepository extends BaseRepository<WarehouseHistory, Long> {
}
