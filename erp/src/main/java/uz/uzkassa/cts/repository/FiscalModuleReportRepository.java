package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.FiscalModuleReport;
import uz.uzkassa.cts.repository.custom.FiscalModuleReportRepositoryCustom;

import java.util.Optional;

@Repository
public interface FiscalModuleReportRepository extends BaseRepository<FiscalModuleReport, Long>, FiscalModuleReportRepositoryCustom {

    Optional<FiscalModuleReport> findFirstByFmSerialNumber(String serialNumber);

    boolean existsByFmSerialNumber(String fmSerialNumber);
}
