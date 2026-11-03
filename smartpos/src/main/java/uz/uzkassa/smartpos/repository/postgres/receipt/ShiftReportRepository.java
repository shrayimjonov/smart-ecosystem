package uz.uzkassa.smartpos.repository.postgres.receipt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.receipt.ShiftReport;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ShiftReportRepository extends JpaRepository<ShiftReport, Long>, ShiftReportRepositoryCustom, JpaSpecificationExecutor<ShiftReport> {
    Optional<ShiftReport> findFirstByTerminalIdOrderByEndDateTimeDesc(Long terminalId);

    Optional<ShiftReport> findFirstByTerminalIdAndNumberAndEndDateTime(Long terminalId, Long number, LocalDateTime endDateTime);

    Optional<ShiftReport> findFirstByBranchIdAndNumberOrderByEndDateTimeDesc(Long branchId, Long number);

    Optional<ShiftReport> findFirstByBranchIdOrderByStartDateTimeDesc(Long branchId);
}
