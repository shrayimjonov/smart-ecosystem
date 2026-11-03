package uz.uzkassa.horeca.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import uz.uzkassa.horeca.domain.techcard.TechCard;


public interface TechCardRepository extends BaseRepository<TechCard, Long>, TechCardRepositoryCustom {
    boolean existsByProductIdAndSizeAndUnitIdAndDeletedIsFalse(Long productId, Double size, Long unitId);

    boolean existsByProductIdAndSizeAndUnitIdAndDeletedIsFalseAndIdNot(Long productId, Double size, Long unitId, Long id);

    Optional<TechCard> findFirstByProductIdAndDeletedIsFalse(Long productId);

    List<TechCard> findAllByProductIdAndDeletedIsFalse(Long productId);

    List<TechCard> findAllByProductIdInAndDeletedIsFalse(Collection<Long> productIds);
}
