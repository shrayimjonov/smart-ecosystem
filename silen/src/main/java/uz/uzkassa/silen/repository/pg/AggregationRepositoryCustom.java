package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.Aggregation;
import uz.uzkassa.silen.dto.TopSelectItem;
import uz.uzkassa.silen.dto.dashboard.DashboardStatsDTO;
import uz.uzkassa.silen.dto.filter.AggregationFilter;
import uz.uzkassa.silen.dto.filter.Filter;
import uz.uzkassa.silen.dto.filter.TopFilter;
import uz.uzkassa.silen.dto.marking.AggregationStatsDTO;
import uz.uzkassa.silen.dto.partyaggregation.AggregationStateMessage;

import java.util.List;

public interface AggregationRepositoryCustom {
    Page<Aggregation> findAllByFilter(AggregationFilter filter);

    AggregationStatsDTO getStats(AggregationFilter filter);

    List<AggregationStateMessage> getPartyAggregations(String partyAggregationId);

    List<String> getOrganizationIdsByFilter(AggregationFilter filter);

    List<TopSelectItem> topErrors(Filter filter);

    List<DashboardStatsDTO> dashboardStats(TopFilter filter);
}
