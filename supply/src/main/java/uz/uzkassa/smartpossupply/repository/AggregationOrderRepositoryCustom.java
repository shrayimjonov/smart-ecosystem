package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.aggregation.AggregationOrder;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.AggregationOrderFilter;
import uz.uzkassa.smartpossupply.filters.BaseFilter;

@Repository
public interface AggregationOrderRepositoryCustom {

    ResultList<AggregationOrder> getResultList(AggregationOrderFilter filter);

    AggregationStatisticsDTO getStatistics(BaseFilter filter);
}
