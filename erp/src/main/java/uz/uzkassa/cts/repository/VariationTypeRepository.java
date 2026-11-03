package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.inventory.VariationType;
import uz.uzkassa.cts.repository.custom.VariationTypeRepositoryCustom;

@Repository
public interface VariationTypeRepository extends BaseRepository<VariationType, Long>, VariationTypeRepositoryCustom {
}
