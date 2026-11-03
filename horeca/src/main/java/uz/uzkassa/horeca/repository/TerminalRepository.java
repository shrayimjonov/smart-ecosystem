package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.Terminal;

import java.util.List;
import java.util.Optional;

public interface TerminalRepository extends BaseRepository<Terminal, Long>, TerminalRepositoryCustom {

    Optional<Terminal> findFirstByBranchIdAndSerialNumberAndFiscalModuleSerialNumberAndDeletedIsFalse(Long branchId, String serialNumber, String fiscalSerialNumber);

    List<Terminal> findAllByBranchIdAndDeletedIsFalse(Long branchId);

    Optional<Terminal> findFirstBySerialNumberAndDeletedIsFalse(String serialNumber);

    Optional<Terminal> findFirstBySerialNumberAndBranchIdAndDeletedIsFalse(String serialNumber, Long branchId);

    Optional<Terminal> findFirstByBranchIdAndDeletedIsFalse(Long branchId);

    Optional<Terminal> findFirstBySerialNumber(String serialNumber);
}
