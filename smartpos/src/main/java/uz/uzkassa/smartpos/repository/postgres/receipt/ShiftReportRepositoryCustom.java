package uz.uzkassa.smartpos.repository.postgres.receipt;

import uz.uzkassa.smartpos.domain.postgres.receipt.ShiftReport;
import uz.uzkassa.smartpos.dto.shift.ShiftReportStatDTO;
import uz.uzkassa.smartpos.filter.ShiftReportFilter;
import uz.uzkassa.smartpos.filter.specification.ShiftReportSpecificationFilter;

import java.util.Collection;

public interface ShiftReportRepositoryCustom {
    ShiftReportStatDTO getReportStat(ShiftReportSpecificationFilter filter);

    ShiftReportStatDTO getReportStat(ShiftReportFilter filter);

    ShiftReport getLastShift(Collection<Long> branchIds, Long companyId);
}
