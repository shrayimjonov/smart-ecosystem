package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.hrms.Dependent;
import uz.uzkassa.cts.repository.custom.DependentRepositoryCustom;

@Repository
public interface DependentRepository extends BaseRepository<Dependent, Long>, DependentRepositoryCustom {
}
