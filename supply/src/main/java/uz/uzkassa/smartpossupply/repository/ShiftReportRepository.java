package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.shift.ShiftReport;

import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 28.12.2022 13:31
 */

@Repository
public interface ShiftReportRepository extends BaseRepository<ShiftReport, Long>, ShiftReportRepositoryCustom {

    Optional<ShiftReport> findFirstByBranchIdAndShiftId(Long branchId, Long shiftId);
}
