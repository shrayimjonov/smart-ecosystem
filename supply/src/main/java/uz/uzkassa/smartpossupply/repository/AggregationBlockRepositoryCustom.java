package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.aggregation.AggregationBlock;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.AggregationBlockFilter;

@Repository
public interface AggregationBlockRepositoryCustom {
    ResultList<AggregationBlock> getResultList(AggregationBlockFilter filter);
}
