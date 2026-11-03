package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.discount.DiscountItem;
import uz.uzkassa.smartpossupply.enums.CommonStatus;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 16.12.2024
 */
@Repository
public interface DiscountItemRepository extends BaseRepository<DiscountItem, Long> {

    @Modifying
    @Query("update DiscountItem set deleted=true where discountId=:discountId")
    void deleteAllByDiscountId(@Param("discountId") Long id);

    @Query("select di from DiscountItem di where di.discount.status=:discountStatus and di.productId=:productId and di.deleted=false and di.discount.deleted=false and di.discount.validTo>=:validTo order by di.id desc limit 1")
    Optional<DiscountItem> findFirstByProductIdAndDiscountStatusAndDiscountValidToIsBefore(@Param("productId") Long productId, @Param("discountStatus") CommonStatus discountStatus, @Param("validTo") LocalDateTime validTo);

}