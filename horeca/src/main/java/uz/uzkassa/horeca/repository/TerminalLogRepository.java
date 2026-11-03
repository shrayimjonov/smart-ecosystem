package uz.uzkassa.horeca.repository;


import java.util.Optional;
import uz.uzkassa.horeca.domain.terminal.TerminalLog;

public interface TerminalLogRepository extends BaseRepository<TerminalLog, Long> {
    Optional<TerminalLog> findFirstBySerialNumber(String serialNumber);

    boolean existsBySerialNumber(String serialNumber);
}
