package uz.uzkassa.cts.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.dto.promomanagardashboard.*;
import uz.uzkassa.cts.enums.FeedBackStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.CustomerBranchApplicationRepository;
import uz.uzkassa.cts.repository.FeedbackRepository;
import uz.uzkassa.cts.service.AdminPromoManagerDashboardService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AdminPromoManagerDashboardServiceImpl implements AdminPromoManagerDashboardService {

    FeedbackRepository feedbackRepository;
    CustomerBranchApplicationRepository customerBranchApplicationRepository;

    @Override
    public List<FeedbackByRegionTypeDTO> getFeedbackCountByRegionAndType(BaseFilter filter) {
        List<FeedbackCountByRegionTypeDTO> list = feedbackRepository.getFeedbackCountByRegionAndType(filter);
        Map<String, List<FeedbackTypeCountDTO>> groupedData = list
                .stream()
                .collect(Collectors.groupingBy(FeedbackCountByRegionTypeDTO::getRegion, Collectors.mapping(data ->
                                new FeedbackTypeCountDTO(data.getType(), data.getCount()),
                        Collectors.toList())));

        return groupedData.entrySet().stream()
                .map(entry -> new FeedbackByRegionTypeDTO(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Override
    public List<FeedbackByRegionAndStatusDTO> getFeedbackCountByRegionAndStatus(BaseFilter filter) {
        List<FeedbackCountByRegionAndStatusDTO> resultList = feedbackRepository.getFeedbackCountByRegionAndStatus(filter);
        Map<String, List<StatusCountDTO>> groupedData = resultList
                .stream()
                .collect(Collectors.groupingBy(FeedbackCountByRegionAndStatusDTO::getRegion, Collectors.mapping(data ->
                                new StatusCountDTO(FeedBackStatus.get(data.getStatus()).getName(), data.getCount()),
                        Collectors.toList())));
        return groupedData.entrySet().stream()
                .map(entry -> new FeedbackByRegionAndStatusDTO(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Override
    public List<DateStatsDTO> getFeedbacksCountByMonth(Integer year, Month month) {
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        LocalDate lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth());
        return groupResultList(feedbackRepository.getFeedbackCountByDate(firstDayOfMonth.atStartOfDay(), lastDayOfMonth.atTime(LocalTime.MAX)));
    }

    @Override
    public List<DateStatsDTO> getCustomerApplicationCountByMonth(Integer year, Month month) {
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        LocalDate lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth());
        return groupResultList(customerBranchApplicationRepository.getCustomerApplicationCountByDate(firstDayOfMonth.atStartOfDay(), lastDayOfMonth.atTime(LocalTime.MAX)));
    }

    @Override
    public List<RegionRateDTO> getRegionRateByFeedback(BaseFilter filter) {
        return feedbackRepository.getRegionAverageRateByFeedback(filter);
    }

    @Override
    public List<TopRegionsByCountDTO> getTopRegionsByFeedback(BaseFilter filter) {
        return feedbackRepository.getTopRegionsByFeedback(filter);
    }

    @Override
    public List<TopRegionsByCountDTO> getTopRegionsByApplications(BaseFilter filter) {
        return customerBranchApplicationRepository.getTopRegionsByApplications(filter);
    }

    private List<DateStatsDTO> groupResultList(List<CountByDateAndRegionDTO> resultList) {
        List<DateStatsDTO> result = new ArrayList<>();
        Map<String, List<DateCountDTO>> groupedData = new HashMap<>();
        resultList.forEach(dateRegionCountDTO -> groupedData
                .computeIfAbsent(dateRegionCountDTO.getRegion(), k -> new ArrayList<>())
                .add(new DateCountDTO(dateRegionCountDTO.getDate(), dateRegionCountDTO.getCount())));
        for (Map.Entry<String, List<DateCountDTO>> entry : groupedData.entrySet()) {
            result.add(new DateStatsDTO(entry.getKey(), entry.getValue()));
        }
        return result;
    }
}
