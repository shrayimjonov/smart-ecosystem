package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.stock.StockItemVariationOption;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.util.List;

@Repository
public interface StockItemVariationOptionRepository extends BaseRepository<StockItemVariationOption, Long> {

    @Modifying
    @Query("update StockItemVariationOption set deleted = true  where entityId =:entityId and entityType =:entityType")
    void deleteAllByEntityIdAndEntityType(@Param("entityId") Long entityId, @Param("entityType") EntityTypeEnum entityType);

    List<StockItemVariationOption> findAllByEntityIdAndEntityTypeAndDeletedIsFalse(Long entityId, EntityTypeEnum entityType);

    @Query("select s.variationOptionId from StockItemVariationOption s where s.entityId =:entityId and s.entityType =:entityType")
    List<Long> findAllVariationOptionIdsByEntityIdAndEntityTypeAndDeletedIsFalse(@Param("entityId") Long entityId, @Param("entityType") EntityTypeEnum entityType);
}
