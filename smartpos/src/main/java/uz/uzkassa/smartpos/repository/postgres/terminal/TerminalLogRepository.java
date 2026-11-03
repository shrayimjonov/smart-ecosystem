package uz.uzkassa.smartpos.repository.postgres.terminal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import uz.uzkassa.smartpos.domain.postgres.terminal.TerminalLog;

import java.util.Optional;

public interface TerminalLogRepository extends JpaRepository<TerminalLog, Long>, JpaSpecificationExecutor<TerminalLog> {
    Optional<TerminalLog> findFirstBySerialNumber(String serialNumber);

    boolean existsBySerialNumber(String serialNumber);
}
