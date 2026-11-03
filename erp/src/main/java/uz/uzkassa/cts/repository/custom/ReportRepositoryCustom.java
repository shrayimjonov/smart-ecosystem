package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.address.Region;
import uz.uzkassa.cts.dto.report.StatisticsByActivityTypeDTO;
import uz.uzkassa.cts.dto.report.TotalBonusDTO;
import uz.uzkassa.cts.filter.ActivityTypeReportFilter;
import uz.uzkassa.cts.filter.BaseReportFilter;
import uz.uzkassa.cts.filter.EmployeeReportFilter;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface ReportRepositoryCustom {

    Map<String, HashMap<Long, BigInteger>> getStatisticsByBranchReport(BaseReportFilter filter);

    List<StatisticsByActivityTypeDTO> getCustomersByActivityType(ActivityTypeReportFilter filter);

    List<StatisticsByActivityTypeDTO> getCustomersByActivityTypeAndRegion(ActivityTypeReportFilter filter, Region region);

    Map<String, HashMap<Long, Long>> getStatisticsByRegionReport(BaseReportFilter filter);

    Map<String, HashMap<Long, Long>> getStatisticsByDistrictReport(BaseReportFilter filter, Region region);

    Map<String, HashMap<Long, Long>> getStatisticsByEmployeeReport(EmployeeReportFilter filter, LocalDateTime fromDate, LocalDateTime toDate);

    HashMap<Long, List<TotalBonusDTO>> getServicesMap(EmployeeReportFilter filter, LocalDateTime fromDate, LocalDateTime toDate);

    HashMap<Long, List<TotalBonusDTO>> getTariffsMap(EmployeeReportFilter filter, LocalDateTime fromDate, LocalDateTime toDate);

    HashMap<Long, List<TotalBonusDTO>> getLeasingMap(EmployeeReportFilter filter, LocalDateTime fromDate, LocalDateTime toDate);

    HashMap<Long, List<TotalBonusDTO>> getTradeInMap(EmployeeReportFilter filter, LocalDateTime fromDate, LocalDateTime toDate);

    HashMap<Long, List<TotalBonusDTO>> getRent(EmployeeReportFilter filter, LocalDateTime fromDate, LocalDateTime toDate);
}
