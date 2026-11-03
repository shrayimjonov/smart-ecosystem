package uz.uzkassa.cts.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.domain.Vat;
import uz.uzkassa.cts.enums.CommonStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface VatRepository extends BaseRepository<Vat, Long> {

    Optional<Vat> findFirstByStatus(CommonStatus status);

    Optional<Vat> findFirstByPercent(BigDecimal percent);

    boolean existsByPercent(BigDecimal percent);

    boolean existsByPercentAndIdNot(BigDecimal percent, Long id);

    @Cacheable(cacheNames = CacheConstants.VAT_ENTITY_LIST_CACHE)
    List<Vat> findAllByDeletedIsFalse();

    @Cacheable(cacheNames = CacheConstants.VAT_ENTITY_LIST_BY_STATUS_CACHE)
    List<Vat> findAllByStatusAndDeletedIsFalse(CommonStatus status);

    @Modifying
    @Query("update Vat set status=:status where id <>:id")
    void updateStatus(@Param("id") Long id, @Param("status") CommonStatus status);
}
