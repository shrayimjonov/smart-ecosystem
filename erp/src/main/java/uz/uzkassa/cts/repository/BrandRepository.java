package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.inventory.Brand;
import uz.uzkassa.cts.repository.custom.BrandRepositoryCustom;

@Repository
public interface BrandRepository extends BaseRepository<Brand, Long>, BrandRepositoryCustom {

    boolean existsByNameIsIgnoreCase(String name);

    boolean existsByNameIsIgnoreCaseAndIdNot(String name, Long id);
}
