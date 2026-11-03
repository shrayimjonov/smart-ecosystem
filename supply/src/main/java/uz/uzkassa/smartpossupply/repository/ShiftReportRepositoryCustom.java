package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.shift.ShiftReport;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.shift.RegistrationReportDTO;
import uz.uzkassa.smartpossupply.dto.shift.ShiftReportStatisticDTO;
import uz.uzkassa.smartpossupply.dto.shift.XReportDTO;
import uz.uzkassa.smartpossupply.dto.shift.ZReportStatisticsDTO;
import uz.uzkassa.smartpossupply.filters.ShiftReportFilter;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 28.12.2022 13:31
 */
@Repository
public interface ShiftReportRepositoryCustom {
    XReportDTO xReport(Long branchId, Long shiftId);

    ZReportStatisticsDTO getZReportStatistics(Long branchId, Long shiftNumber);

    ResultList<ShiftReport> zReport(ShiftReportFilter filter);

    ShiftReportStatisticDTO getStatistics(ShiftReportFilter filter);

    RegistrationReportDTO getRegistrationReport(Long branchId);
}
