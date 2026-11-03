package uz.uzkassa.smartpossupply.repository;


import uz.uzkassa.smartpossupply.domain.Terminal;

import java.util.List;
import java.util.Optional;

public interface TerminalRepository extends BaseRepository<Terminal, Long>, TerminalRepositoryCustom {

    List<Terminal> findByBranch_CompanyIdIn(List<Long> companyIds);

    Optional<Terminal> findFirstBySerialNumber(String serialNumber);

    List<Terminal> findAllByCompanyIdAndSerialNumberIn(Long companyId, List<String> serialNumber);
}
