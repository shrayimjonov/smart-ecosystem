package uz.uzkassa.horeca.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.uzkassa.horeca.domain.factura.Factura;
import uz.uzkassa.horeca.dto.factura.Status;

import java.util.List;
import java.util.Optional;

public interface FacturaRepository extends BaseRepository<Factura, Long>, FacturaRepositoryCustom {
    Optional<Factura> findFirstByFacturaIdAndDeletedIsFalse(String facturaId);

    boolean existsByFacturaId(String facturaId);

    @Query("SELECT coalesce(max(f.incrementNumber), 0) FROM Factura f where f.companyId=:companyId")
    Long getMaxIncrementNumber(@Param("companyId") Long companyId);

    @Query("select id from Factura where status=:status and sellerId=:sellerId and deleted = false")
    List<Long> findAllIdsByStatusAndSellerId(@Param("status") Status status, @Param("sellerId") Long sellerId);

    @Query("select id from Factura where status=:status and buyerId=:buyerId and deleted = false")
    List<Long> findAllIdsByStatusAndBuyerId(@Param("status") Status status, @Param("buyerId") Long buyerId);
}
