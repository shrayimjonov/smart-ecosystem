package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.aggregation.InvalidAggregation;

import java.util.List;

@Repository
public interface InvalidAggregationRepository extends BaseRepository<InvalidAggregation, Long> {
    List<InvalidAggregation> findAllByStockProductId(Long stockProductId);

    boolean existsByStockProductId(Long stockProductId);

    @Modifying
    void deleteAllByStockProductId(Long stockProductId);

    @Modifying
    void deleteAllByStockId(Long stockId);
}
