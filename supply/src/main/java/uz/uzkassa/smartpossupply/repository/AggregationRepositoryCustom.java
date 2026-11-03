package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.aggregation.Aggregation;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationUsedStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.AggregationFilter;

import java.util.List;

@Repository
public interface AggregationRepositoryCustom {

    ResultList<Aggregation> getResultList(AggregationFilter filter);

    List<String> getCodesByAggregationOrder(AggregationFilter filter);

    Long nextVal(String gcp);

    AggregationStatisticsDTO getStatistics(AggregationFilter filter);

    List<AggregationUsedStatisticsDTO> getUsedCountByAggregationOrderIdIn(List<Long> idList);
}
