package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.hrms.TimeTrack;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.report.TimeTrackReportDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.TimeTrackReportFilter;

@Repository
public interface TimeTrackRepositoryCustom {

    ResultList<TimeTrack> getResultList(BaseFilter filter);

    ResultList<TimeTrackReportDTO> getReport(TimeTrackReportFilter filter);
}
