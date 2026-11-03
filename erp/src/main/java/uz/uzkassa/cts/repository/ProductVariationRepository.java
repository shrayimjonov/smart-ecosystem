package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.inventory.ProductVariation;
import uz.uzkassa.cts.repository.custom.ProductVariationRepositoryCustom;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariationRepository extends BaseRepository<ProductVariation, Long>, ProductVariationRepositoryCustom {

    Optional<ProductVariation> findFirstByProductIdAndVariationOptionIdAndDeletedIsFalse(Long productId, Long variationOptionId);

    List<ProductVariation> findAllByProductIdAndDeletedIsFalse(Long productId);
}
