package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.AggregationHistory;
import uz.uzkassa.silen.dto.MaterialReportDTO;
import uz.uzkassa.silen.dto.MaterialReportFilter;
import uz.uzkassa.silen.dto.filter.AggregationFilter;
import uz.uzkassa.silen.dto.marking.AggregationStatsDTO;

public interface AggregationHistoryRepositoryCustom {
    Page<AggregationHistory> findAllByFilter(AggregationFilter filter);

    AggregationStatsDTO getStats(AggregationFilter filter);

    Page<MaterialReportDTO> materialReport(MaterialReportFilter filter);
}
