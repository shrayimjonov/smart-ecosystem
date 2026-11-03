package uz.uzkassa.horeca.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.uzkassa.horeca.domain.receipt.Receipt;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface ReceiptRepository extends BaseRepository<Receipt, Long>, ReceiptRepositoryCustom {

    @Query("select distinct r.uid from Receipt r where r.status <> 'DRAFT' and r.uid in :uidList")
    Set<String> getAlreadyUsedUidList(@Param("uidList") Collection<String> uidList);

    Optional<Receipt> findFirstByUidAndDeletedIsFalse(String uid);

    Optional<Receipt> findFirstByFiscalUrlAndDeletedIsFalse(String fiscalUrl);

    Optional<Receipt> findFirstByOrderIdAndDeletedIsFalse(Long orderId);
    Optional<Receipt> findFirstByOrderIdAndDeletedIsFalseAndIdNot(Long orderId, Long id);
}
