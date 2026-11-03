package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.InactiveFiscalModule;

import java.util.Optional;

@Repository
public interface InactiveFiscalModuleRepository extends BaseRepository<InactiveFiscalModule, Long> {

    Optional<InactiveFiscalModule> findFirstByFiscalModuleId(Long fiscalModuleId);
}
