package uz.uzkassa.smartpossupply.repository;

import uz.uzkassa.smartpossupply.domain.TerminalLog;

import java.util.Optional;

public interface TerminalLogRepository extends BaseRepository<TerminalLog, Long>, TerminalLogRepositoryCustom {

    Optional<TerminalLog> findFirstBySerialNumber(String serialNumber);

    boolean existsBySerialNumber(String serialNumber);
}
