package uz.uzkassa.horeca.repository.terminal;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import uz.uzkassa.horeca.domain.terminal.TerminalShiftReport;
import uz.uzkassa.horeca.repository.BaseRepository;

@Repository
public interface TerminalShiftReportRepository extends BaseRepository<TerminalShiftReport, Long> {
    Optional<TerminalShiftReport> findFirstByTerminalIdOrderByEndDateTimeDesc(Long terminalId);

    Optional<TerminalShiftReport> findFirstByTerminalIdAndNumberAndEndDateTime(Long terminalId, Long number, LocalDateTime endDateTime);

    Optional<TerminalShiftReport> findFirstByBranchIdAndNumberOrderByEndDateTimeDesc(Long branchId, Long number);

    Optional<TerminalShiftReport> findFirstByBranchIdOrderByStartDateTimeDesc(Long branchId);
}
