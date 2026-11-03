package uz.uzkassa.cts.service;

import org.springframework.core.io.Resource;
import uz.uzkassa.cts.constants.MessageConstants;
import uz.uzkassa.cts.dto.report.*;
import uz.uzkassa.cts.filter.ActivityTypeReportFilter;
import uz.uzkassa.cts.filter.BaseReportFilter;
import uz.uzkassa.cts.filter.EmployeeReportFilter;

import java.util.List;

public interface ReportService extends MessageConstants {

    List<StatisticsByActivityTypeReportDTO> getStatisticsByActivityTypeReport(ActivityTypeReportFilter filter);

    List<StatisticsByRegionReportDTO> getStatisticsByRegionReport(BaseReportFilter filter);

    List<StatisticsByBranchReportDTO> getStatisticsByBranchReport(BaseReportFilter filter);

    List<StatisticsByDistrictReportDTO> getStatisticsByDistrictReport(BaseReportFilter filter);

    List<StatisticsByEmployeeReportDTO> getStatisticsByEmployeeReport(EmployeeReportFilter filter);

    Resource exportStatisticsByEmployeeReport(EmployeeReportFilter filter);
}
