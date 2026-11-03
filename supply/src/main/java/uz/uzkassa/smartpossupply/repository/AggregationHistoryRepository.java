package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.aggregation.AggregationHistory;

@Repository
public interface AggregationHistoryRepository extends BaseRepository<AggregationHistory, Long>, AggregationHistoryRepositoryCustom {
}
