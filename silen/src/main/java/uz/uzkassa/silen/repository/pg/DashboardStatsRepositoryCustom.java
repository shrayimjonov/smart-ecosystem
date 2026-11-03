package uz.uzkassa.silen.repository.pg;

import uz.uzkassa.silen.dto.dashboard.DashboardStatsDTO;
import uz.uzkassa.silen.dto.filter.TopFilter;

import java.util.List;


public interface DashboardStatsRepositoryCustom {
    List<DashboardStatsDTO> findAllByFilter(TopFilter filter);
}
