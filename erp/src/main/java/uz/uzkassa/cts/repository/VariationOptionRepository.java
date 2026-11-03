package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.inventory.VariationOption;
import uz.uzkassa.cts.repository.custom.VariationOptionRepositoryCustom;

@Repository
public interface VariationOptionRepository extends BaseRepository<VariationOption, Long>, VariationOptionRepositoryCustom {
}
