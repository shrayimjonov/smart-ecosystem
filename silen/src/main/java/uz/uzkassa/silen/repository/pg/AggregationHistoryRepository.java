package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.AggregationHistory;

@Repository
public interface AggregationHistoryRepository extends JpaRepository<AggregationHistory, String>, AggregationHistoryRepositoryCustom {

}
