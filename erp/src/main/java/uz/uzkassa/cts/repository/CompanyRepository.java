package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Company;
import uz.uzkassa.cts.repository.custom.CompanyRepositoryCustom;

@Repository
public interface CompanyRepository extends BaseRepository<Company, Long>, CompanyRepositoryCustom {

    Company findByTin(String tin);

    boolean existsByTin(String tin);

    boolean existsByTinAndIdNot(String tin, Long id);

    boolean existsByNameIsIgnoreCase(String name);

    boolean existsByNameIsIgnoreCaseAndIdNot(String name, Long id);
}
