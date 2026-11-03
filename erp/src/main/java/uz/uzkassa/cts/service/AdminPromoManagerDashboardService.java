package uz.uzkassa.cts.service;

import uz.uzkassa.cts.dto.promomanagardashboard.DateStatsDTO;
import uz.uzkassa.cts.dto.promomanagardashboard.TopRegionsByCountDTO;
import uz.uzkassa.cts.dto.promomanagardashboard.FeedbackByRegionTypeDTO;
import uz.uzkassa.cts.dto.promomanagardashboard.RegionRateDTO;
import uz.uzkassa.cts.dto.promomanagardashboard.FeedbackByRegionAndStatusDTO;
import uz.uzkassa.cts.filter.BaseFilter;

import java.time.Month;
import java.util.List;

public interface AdminPromoManagerDashboardService {

    List<FeedbackByRegionTypeDTO> getFeedbackCountByRegionAndType(BaseFilter filter);

    List<FeedbackByRegionAndStatusDTO> getFeedbackCountByRegionAndStatus(BaseFilter filter);

    List<DateStatsDTO> getFeedbacksCountByMonth(Integer year, Month month);

    List<DateStatsDTO> getCustomerApplicationCountByMonth(Integer year, Month month);

    List<RegionRateDTO> getRegionRateByFeedback(BaseFilter filter);

    List<TopRegionsByCountDTO> getTopRegionsByFeedback(BaseFilter filter);

    List<TopRegionsByCountDTO> getTopRegionsByApplications(BaseFilter filter);
}
