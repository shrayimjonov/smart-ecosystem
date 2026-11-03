package uz.uzkassa.cts.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.BranchFiscalModule;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.repository.custom.BranchFiscalModuleRepositoryCustom;

import java.util.Optional;

@Repository
public interface BranchFiscalModuleRepository extends BaseRepository<BranchFiscalModule, Long>, BranchFiscalModuleRepositoryCustom {

    BranchFiscalModule findFirstByBranchIdAndFiscalModuleIdAndStatus(Long branchId, Long fiscalModuleId, DeviceStatus status);

    BranchFiscalModule findFirstByBranchIdAndFiscalModuleId(Long branchId, Long fiscalModuleId);

    Optional<BranchFiscalModule> findFirstByFiscalModuleIdOrderByIdDesc(Long fiscalModuleId);

    Long countAllByBranchId(Long branchId);
}
