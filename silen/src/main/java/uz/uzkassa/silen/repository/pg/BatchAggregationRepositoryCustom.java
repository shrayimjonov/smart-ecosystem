package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.BatchAggregation;
import uz.uzkassa.silen.dto.filter.BatchFilter;
import uz.uzkassa.silen.dto.marking.BatchAggregationListDTO;

public interface BatchAggregationRepositoryCustom {
    Page<BatchAggregation> findAllByFilter(BatchFilter filter);

    Page<BatchAggregationListDTO> findAllByFilterBatch(BatchFilter filter);

    Long getStats(BatchFilter filter);
}
