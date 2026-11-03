package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.factura.Factura;
import uz.uzkassa.smartpossupply.dto.factura.Status;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaRepository extends BaseRepository<Factura, Long>, FacturaRepositoryCustom {

    Optional<Factura> findFirstByFacturaIdAndDeletedIsFalse(String facturaId);

    boolean existsByFacturaId(String facturaId);

    @Query("SELECT coalesce(max(f.incrementNumber), 0) FROM Factura f where f.companyId=:companyId")
    Long getMaxIncrementNumber(@Param("companyId") Long companyId);

    @Query("SELECT count(f.id) FROM Factura f where f.deleted =false and f.companyId=:companyId and f.status not in (:statuses)")
    Long getTotalStatistics(@Param("companyId") Long companyId, @Param("statuses") List<Status> statuses);

    @Query("select f.id from Factura f where f.status=:status and f.sellerId=:sellerId and f.deleted = false")
    List<Long> findAllIdsByStatusAndSellerId(@Param("status") Status status, @Param("sellerId") Long sellerId);

    @Query("select id from Factura where status=:status and buyerId=:buyerId and deleted = false")
    List<Long> findAllIdsByStatusAndBuyerId(@Param("status") Status status, @Param("buyerId") Long buyerId);
}
