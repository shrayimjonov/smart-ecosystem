package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.dto.dashboard.response.DashboardCustomerBranchDeviceDto;
import uz.uzkassa.cts.dto.dashboard.response.DashboardStatistics;
import uz.uzkassa.cts.dto.dashboard.response.DashboardStatsByDistrictResponseDto;
import uz.uzkassa.cts.dto.dashboard.response.DashboardStatsByRegionResponseDto;
import uz.uzkassa.cts.filter.DashboardFilter;

import java.util.List;

public interface DashboardService {
    List<DashboardStatistics> getStatusesByTariff();

    List<DashboardStatsByRegionResponseDto> getStatsByRegions();

    List<DashboardStatsByDistrictResponseDto> getStatsByRegion(Long id);

    Page<DashboardCustomerBranchDeviceDto> getStatsByRegionTariffAndStatus(DashboardFilter filter);
}
