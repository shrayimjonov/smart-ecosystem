package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.receipt.Receipt;

import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 21.12.2022 18:41
 */
@Repository
public interface ReceiptRepository extends BaseRepository<Receipt, Long>, ReceiptRepositoryCustom {
    Optional<Receipt> findFirstByUid(String uid);

    @Query("select r from Receipt r where r.uid=:uid")
    @EntityGraph(attributePaths = {"company", "branch", "branch.address", "creator", "items", "items.product", "items.product.unit"})
    Optional<Receipt> getDetail(@Param("uid") String uid);

    Optional<Receipt> findFirstByStockOutIdAndDeletedIsFalse(Long stockOutId);

    boolean existsByStockOutIdAndDeletedIsFalse(Long stockOutId);

    boolean existsByStockReturnIdAndDeletedIsFalse(Long stockReturnId);
}
