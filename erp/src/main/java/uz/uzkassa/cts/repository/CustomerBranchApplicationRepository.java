package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.CustomerBranchApplication;
import uz.uzkassa.cts.repository.custom.CustomerBranchApplicationRepositoryCustom;

@Repository
public interface CustomerBranchApplicationRepository extends BaseRepository<CustomerBranchApplication, Long>, CustomerBranchApplicationRepositoryCustom {
}
