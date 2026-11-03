package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.customer.Feedback;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.promomanagardashboard.CountByDateAndRegionDTO;
import uz.uzkassa.cts.dto.promomanagardashboard.FeedbackCountByRegionTypeDTO;
import uz.uzkassa.cts.dto.promomanagardashboard.FeedbackCountByRegionAndStatusDTO;
import uz.uzkassa.cts.dto.promomanagardashboard.TopRegionsByCountDTO;
import uz.uzkassa.cts.dto.promomanagardashboard.RegionRateDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.FeedbackFilter;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

@Repository
public interface FeedbackRepositoryCustom {

    ResultList<Feedback> getResultList(FeedbackFilter filter);

    List<FeedbackCountByRegionTypeDTO> getFeedbackCountByRegionAndType(BaseFilter filter);

    List<FeedbackCountByRegionAndStatusDTO> getFeedbackCountByRegionAndStatus(BaseFilter filter);

    List<CountByDateAndRegionDTO> getFeedbackCountByDate(LocalDateTime from, LocalDateTime to);

    List<RegionRateDTO> getRegionAverageRateByFeedback(BaseFilter filter);

    List<TopRegionsByCountDTO> getTopRegionsByFeedback(BaseFilter filter);
}
