package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationHistoryListDTO;
import uz.uzkassa.smartpossupply.filters.ClickhouseFilter;

@Repository
public interface AggregationHistoryRepositoryCustom {

    Page<AggregationHistoryListDTO> getResultList(ClickhouseFilter filter);
}
