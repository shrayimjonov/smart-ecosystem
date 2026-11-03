package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.promomanagardashboard.DateStatsDTO;
import uz.uzkassa.cts.dto.promomanagardashboard.TopRegionsByCountDTO;
import uz.uzkassa.cts.dto.promomanagardashboard.FeedbackByRegionTypeDTO;
import uz.uzkassa.cts.dto.promomanagardashboard.RegionRateDTO;
import uz.uzkassa.cts.dto.promomanagardashboard.FeedbackByRegionAndStatusDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.AdminPromoManagerDashboardService;

import java.time.Month;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminPromoManagerDashboardRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminPromoManagerDashboardResource {

    AdminPromoManagerDashboardService adminPromoManagerDashboardService;

    @GetMapping(ApiConstants.feedbackCountByTypeApi)
    ResponseEntity<List<FeedbackByRegionTypeDTO>> getFeedbackByType(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(adminPromoManagerDashboardService.getFeedbackCountByRegionAndType(filter));
    }

    @GetMapping(ApiConstants.feedbackCountByStatusApi)
    ResponseEntity<List<FeedbackByRegionAndStatusDTO>> getFeedbackByStatus(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(adminPromoManagerDashboardService.getFeedbackCountByRegionAndStatus(filter));
    }

    @GetMapping(ApiConstants.feedbackCountBydMonthApi)
    ResponseEntity<List<DateStatsDTO>> getFeedbackByMonth(@RequestParam Integer year,
                                                          @RequestParam Month month) {
        return ResponseEntity.ok(adminPromoManagerDashboardService.getFeedbacksCountByMonth(year, month));
    }

    @GetMapping(ApiConstants.customerApplicationCountByMonthApi)
    ResponseEntity<List<DateStatsDTO>> getCustomerApplicationByMonth(@RequestParam Integer year,
                                                                     @RequestParam Month month) {
        return ResponseEntity.ok(adminPromoManagerDashboardService.getCustomerApplicationCountByMonth(year, month));
    }

    @GetMapping(ApiConstants.regionRateByFeedbackApi)
    ResponseEntity<List<RegionRateDTO>> getRegionRateByFeedBack(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(adminPromoManagerDashboardService.getRegionRateByFeedback(filter));
    }

    @GetMapping(ApiConstants.topRegionsByFeedbackApi)
    ResponseEntity<List<TopRegionsByCountDTO>> getTopRegionByFeedBack(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(adminPromoManagerDashboardService.getTopRegionsByFeedback(filter));
    }

    @GetMapping(ApiConstants.topRegionsByCustomerApplicationApi)
    ResponseEntity<List<TopRegionsByCountDTO>> getTopRegionByApplication(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(adminPromoManagerDashboardService.getTopRegionsByApplications(filter));
    }
}
