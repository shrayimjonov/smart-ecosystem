package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.CompanyFiscalModule;
import uz.uzkassa.cts.repository.custom.CompanyFiscalModuleRepositoryCustom;

import java.util.Optional;

@Repository
public interface CompanyFiscalModuleRepository extends BaseRepository<CompanyFiscalModule, Long>, CompanyFiscalModuleRepositoryCustom {

    CompanyFiscalModule findFirstByCompanyIdAndFiscalModuleId(Long companyId, Long fiscalModuleId);

    @Query("select count(cfm.id) from CompanyFiscalModule cfm where cfm.deleted=false and cfm.companyId=?1 and cfm.fiscalModuleId= ?2")
    Long totalByFiscalModuleId(Long companyId, Long fiscalModuleId);

    Optional<CompanyFiscalModule> findFirstByFiscalModuleIdOrderByIdDesc(Long fiscalModuleId);
}
