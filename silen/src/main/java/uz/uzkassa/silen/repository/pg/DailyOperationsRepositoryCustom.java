package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.DailyOperations;
import uz.uzkassa.silen.dto.AggregationSum;
import uz.uzkassa.silen.dto.DailyOperationsFilter;
import uz.uzkassa.silen.dto.DailyOperationsMaterialStats;
import uz.uzkassa.silen.dto.DailyOperationsStats;

import java.time.LocalDateTime;
import java.util.List;

public interface DailyOperationsRepositoryCustom {

    Page<DailyOperations> findAllByFilter(DailyOperationsFilter filter);

    DailyOperationsStats getStats(DailyOperationsFilter filter);

    List<DailyOperationsMaterialStats> materialReport(DailyOperationsFilter filter);

    List<AggregationSum> getProductionQuantity(LocalDateTime from, LocalDateTime to);

    List<AggregationSum> getShippedQuantity(LocalDateTime from, LocalDateTime to);

    List<AggregationSum> getStoreInQuantity(LocalDateTime from, LocalDateTime to);

    List<AggregationSum> getDropoutQuantity(LocalDateTime from, LocalDateTime to);
}
