package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.pricelist.PriceListItem;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 04.02.2023 13:44
 */
@Repository
public interface PriceListItemRepository extends BaseRepository<PriceListItem, Long> {

    @Modifying
    @Query("update PriceListItem set deleted = true where priceListId=:priceListId")
    void deleteByPriceListId(@Param("priceListId") Long priceListId);

    boolean existsByProductId(Long productId);
}
