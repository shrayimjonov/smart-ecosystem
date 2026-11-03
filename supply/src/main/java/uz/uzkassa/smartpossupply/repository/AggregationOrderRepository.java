package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.aggregation.AggregationOrder;

@Repository
public interface AggregationOrderRepository extends BaseRepository<AggregationOrder, Long>, AggregationOrderRepositoryCustom {
    @Query("SELECT coalesce(max(ao.incrementNumber), 0) FROM AggregationOrder ao where ao.companyId=:companyId")
    Long getMaxIncrementNumber(@Param("companyId") Long companyId);
}
